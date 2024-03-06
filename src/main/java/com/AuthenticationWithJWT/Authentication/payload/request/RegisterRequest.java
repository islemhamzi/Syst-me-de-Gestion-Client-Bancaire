package com.AuthenticationWithJWT.Authentication.payload.request;


import com.AuthenticationWithJWT.Authentication.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstname;


    private String lastname;


    @Email(message = "Format de l'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    private String agence; // Facultatif, peut être défini plus tard ou avoir une valeur par défaut
}
