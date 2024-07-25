package com.AuthenticationWithJWT.Authentication.dto;

import com.AuthenticationWithJWT.Authentication.enums.AccountStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String matriculeLdap;
    private String matriculeAmplitude;
    private Instant creationDate;
    private AccountStatus accountStatus;
    private List<String> roles;  // Ajouter ce champ

    // Méthode setter pour le champ roles
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
