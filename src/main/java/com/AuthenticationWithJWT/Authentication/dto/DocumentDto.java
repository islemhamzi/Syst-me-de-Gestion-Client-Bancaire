package com.AuthenticationWithJWT.Authentication.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentDto {
    private Long idDocument;
    private String numeroCompte;
    private String codeClient;
    private String typeDocument;
    private LocalDate dateEdition;
    private LocalDate dateComptable;
    private String nomDocument;
    private String cheminDocument;
    private String userMatricule;
    private String agenceCode;
    private String agenceEdition;
    private String userEmail;
    private String delegatorUsername;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long delegationId;
}
