package com.AuthenticationWithJWT.Authentication.service.impl;


import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.enums.TokenType;
import com.AuthenticationWithJWT.Authentication.payload.request.AuthenticationRequest;
import com.AuthenticationWithJWT.Authentication.payload.request.RegisterRequest;
import com.AuthenticationWithJWT.Authentication.payload.response.AuthenticationResponse;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import com.AuthenticationWithJWT.Authentication.service.AuthenticationService;
import com.AuthenticationWithJWT.Authentication.service.JwtService;
import com.AuthenticationWithJWT.Authentication.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Adresse mail manquante");
        }
        // Vérification de l'existence de l'utilisateur
        userRepository.findByMatricule(request.getMatricule()).ifPresent(u -> {
            throw new IllegalArgumentException("Utilisateur déjà inscrit");
        });

        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .matricule(request.getMatricule())
                .role(request.getRole())
                .agence(request.getAgence() != null ? request.getAgence() : "Default Agence") // Affectation d'une agence par défaut
                .build();

        user = userRepository.save(user);
        return generateAuthenticationResponse(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMatricule(), request.getPassword()));
        var user = (User) authentication.getPrincipal();

        return generateAuthenticationResponse(user);
    }

    private AuthenticationResponse generateAuthenticationResponse(User user) {
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        var roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .roles(roles)
                .matricule(user.getMatricule())
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .refreshToken(refreshToken.getToken())
                .tokenType(TokenType.BEARER.name())
                .build();
    }
}
