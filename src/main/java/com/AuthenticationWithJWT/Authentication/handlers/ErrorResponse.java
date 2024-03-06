package com.AuthenticationWithJWT.Authentication.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private Instant timestamp;
    private String message="Matricule ou mot de passe incorrect. Veuillez vérifier vos identifiants";
    private String path;
}
