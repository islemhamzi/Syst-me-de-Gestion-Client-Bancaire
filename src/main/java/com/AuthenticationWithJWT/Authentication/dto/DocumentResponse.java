package com.AuthenticationWithJWT.Authentication.dto;

import com.AuthenticationWithJWT.Authentication.entities.Agence;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DocumentResponse {
    private Long idDocument;
    private String numeroCompte;
    private String codeClient;
    private String typeDocument;
    private LocalDate dateEdition;
    private LocalDate dateComptable;
    private String nomDocument;
    private String cheminDocument;
    private String delegatorUsername;
    private String delegateUsername;
    private Agence agenceEdition;
}
