package com.AuthenticationWithJWT.Authentication.service.impl;

import com.AuthenticationWithJWT.Authentication.entities.Agence;
import com.AuthenticationWithJWT.Authentication.entities.Evuti;
import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.enums.AccountStatus;
import com.AuthenticationWithJWT.Authentication.enums.Privilege;
import com.AuthenticationWithJWT.Authentication.enums.Role;
import com.AuthenticationWithJWT.Authentication.enums.TokenType;
import com.AuthenticationWithJWT.Authentication.payload.request.AuthenticationRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RegisterRequest;
import com.AuthenticationWithJWT.Authentication.payload.response.AuthenticationResponse;
import com.AuthenticationWithJWT.Authentication.repository.AgenceRepository;
import com.AuthenticationWithJWT.Authentication.repository.EvutiRepository;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import com.AuthenticationWithJWT.Authentication.service.ActivityLogService;
import com.AuthenticationWithJWT.Authentication.service.AuthenticationService;
import com.AuthenticationWithJWT.Authentication.service.JwtService;
import com.AuthenticationWithJWT.Authentication.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ActivityLogService activityLogService;
    private final EvutiRepository evutiRepository;
    private final AgenceRepository agenceRepository;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Adresse mail manquante");
        }

        // Check if the user already exists and throw an exception if true
        userRepository.findByMatricule(request.getMatricule()).ifPresent(u -> {
            throw new IllegalArgumentException("Utilisateur déjà inscrit");
        });

        // Create a new user if not exists
        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .matricule(request.getMatricule())
                .roles(request.getRole() != null ? String.join(",", request.getRole().stream().map(Role::name).collect(Collectors.toList())) : "USER") // Default to USER if role is not provided
                .creationDate(Instant.now())  // Initialize creation date
                .accountStatus(AccountStatus.ACTIVE) // Set default account status to ACTIVE
                .build();

        user = userRepository.save(user);  // Save the new user

        // Assuming Evuti and Agence need to be set up here
        // Create and save Evuti
        Evuti evuti = new Evuti();
        evuti.setMatriculeLdap(user.getMatricule());
        evuti.setMatriculeAmplitude(request.getLdapMatricule()); // Or any appropriate value from request
        evuti.setUser(user);


        // Log the registration action
        activityLogService.logActivity(
                String.format("%s %s en tant que ADMIN a enregistré un nouveau utilisateur %s %s",
                        request.getFirstname(), request.getLastname(), user.getFirstName(), user.getLastName()),
                request.getFirstname() + " " + request.getLastname(), "ADMIN", "127.0.0.1" // Example IP
        );

        return generateAuthenticationResponse(user, "Default Agency Label");  // Assuming agence label is fetched correctly
    }


    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMatricule(), request.getPassword()));

        User user = userRepository.findByMatricule(request.getMatricule())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new DisabledException("Votre compte est désactivé");
        }

        // Récupération des informations d'Evuti et d'Agence
        Evuti evuti = evutiRepository.findByMatriculeLdap(user.getMatricule())
                .orElseThrow(() -> new IllegalArgumentException("Evuti record not found"));

        Agence agence = agenceRepository.findById(evuti.getAgence().getCodeAgence())
                .orElseThrow(() -> new IllegalArgumentException("Agence not found"));

        AuthenticationResponse response = generateAuthenticationResponse(user, agence.getLibelleAgence());

        // Log the authentication action
        activityLogService.logActivity(
                String.format("%s %s en tant que %s a authentifié à la date %s dont son adresse IP %s",
                        user.getFirstName(), user.getLastName(), String.join(", ", user.getRoles()), Instant.now().toString(), "127.0.0.1"), // Example IP
                user.getFirstName() + " " + user.getLastName(), String.join(", ", user.getRoles()), "127.0.0.1" // Example IP
        );

        return response;
    }


    private AuthenticationResponse generateAuthenticationResponse(User user, String agenceLibelle) {
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        List<String> roles = Arrays.asList(user.getRoles().split(","));
        List<String> privileges = roles.stream()
                .flatMap(role -> Role.valueOf(role).getPrivileges().stream())
                .map(Privilege::name)
                .collect(Collectors.toList());

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .roles(roles)
                .privileges(privileges)
                .matricule(user.getMatricule())
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .agence(agenceLibelle != null ? agenceLibelle : "Not Available")
                .refreshToken(refreshToken.getToken())
                .tokenType(TokenType.BEARER.name())
                .build();
    }
}
