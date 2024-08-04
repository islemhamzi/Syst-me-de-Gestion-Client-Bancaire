package com.AuthenticationWithJWT.Authentication.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DelegationRequest {
    private String delegatorUsername;
    private String delegateUsername;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long documentId;
}
