package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.dto.UserDto;
import com.AuthenticationWithJWT.Authentication.entities.Evuti;
import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.enums.AccountStatus;
import com.AuthenticationWithJWT.Authentication.repository.EvutiRepository;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final SmsService smsService;
    private final EmailService emailService;
    private final EvutiRepository evutiRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserDto).collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMatriculeLdap(user.getMatricule());
        dto.setMatriculeAmplitude(user.getEvuti() != null ? user.getEvuti().getMatriculeAmplitude() : null);
        dto.setCreationDate(user.getCreationDate());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setRoles(Arrays.asList(user.getRoles().split(",")));
        return dto;
    }

    public void updateUserRoles(String matriculeLdap, List<String> roleNames) throws Exception {
        Optional<User> optionalUser = userRepository.findByMatricule(matriculeLdap);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String oldRoles = user.getRoles();
            user.setRoles(String.join(",", roleNames));
            userRepository.save(user);

            // Log role update action
            String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User adminUser = userRepository.findByMatricule(adminUsername).orElseThrow(() -> new Exception("Admin not found"));
            activityLogService.logActivity(
                    String.format("L'administrateur %s %s a mis à jour les rôles de %s %s de [%s] à [%s] à la date %s dont son adresse IP %s",
                            adminUser.getFirstName(), adminUser.getLastName(), user.getFirstName(), user.getLastName(), oldRoles, String.join(",", roleNames), Instant.now().toString(), "127.0.0.1"),
                    adminUser.getFirstName() + " " + adminUser.getLastName(), "ADMIN", "127.0.0.1"
            );

            // Send notifications
            String message = String.format("Monsieur/Madame %s %s, vos rôles ont été mis à jour à %s.",
                    user.getFirstName(), user.getLastName(), String.join(", ", roleNames));
            logger.info("Sending notifications to user: {}", user.getMatricule());
            smsService.sendSms(user.getPhoneNumber(), message);
            emailService.sendEmail(user.getEmail(), "Mise à jour de votre rôle", message);
        } else {
            throw new Exception("User not found with matriculeLdap: " + matriculeLdap);
        }
    }

    public void updateUserStatus(String matriculeLdap, String status) throws Exception {
        Optional<User> optionalUser = userRepository.findByMatricule(matriculeLdap);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String oldStatus = user.getAccountStatus().name();
            user.setAccountStatus(AccountStatus.valueOf(status));
            userRepository.save(user);

            // Log status update action
            String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User adminUser = userRepository.findByMatricule(adminUsername).orElseThrow(() -> new Exception("Admin not found"));
            activityLogService.logActivity(
                    String.format("L'administrateur %s %s a modifié le statut de %s %s de [%s] à [%s] le %s depuis l'adresse IP %s",
                            adminUser.getFirstName(), adminUser.getLastName(), user.getFirstName(), user.getLastName(), oldStatus, status, Instant.now().toString(), "127.0.0.1"),
                    adminUser.getFirstName() + " " + adminUser.getLastName(), "ADMIN", "127.0.0.1"
            );

            // Send notifications
            String message = String.format("Monsieur/Madame %s %s, votre compte a été %s.",
                    user.getFirstName(), user.getLastName(), status.equals("ACTIVE") ? "activé" : "désactivé");
            logger.info("Sending notifications to user: {}", user.getMatricule());
            smsService.sendSms(user.getPhoneNumber(), message);
            emailService.sendEmail(user.getEmail(), "Mise à jour du statut de votre compte", message);
        } else {
            throw new Exception("User not found with matriculeLdap: " + matriculeLdap);
        }
    }

    public String getCodeAgenceByMatricule(String matricule) {
        logger.info("Fetching agence for user with matricule: {}", matricule);
        Evuti evuti = evutiRepository.findByMatriculeLdap(matricule)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String codeAgence = evuti.getAgence().getCodeAgence();
        logger.info("Fetched codeAgence: {}", codeAgence);
        return codeAgence;
    }

    public String getUserRole(String matricule) {
        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getRoles();
    }
}
