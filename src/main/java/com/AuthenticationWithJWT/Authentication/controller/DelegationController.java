package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.dto.DelegationRequest;
import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/delegations")
public class DelegationController {

    private static final Logger logger = LoggerFactory.getLogger(DelegationController.class);

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Map<String, String>> createDelegation(@RequestBody DelegationRequest delegationRequest, Principal principal, HttpServletRequest request) {
        try {
            delegationService.createDelegation(delegationRequest.getDelegatorUsername(), delegationRequest.getDelegateUsername(), delegationRequest.getStartDate(), delegationRequest.getEndDate(), delegationRequest.getDocumentId());

            String username = principal.getName();
            String role = userService.getUserRole(username);
            String ip = getClientIpAddress(request);
            activityLogService.logDelegationCreated(delegationRequest.getDelegatorUsername(), delegationRequest.getDelegateUsername(), role, ip, delegationRequest.getStartDate().toString(), delegationRequest.getEndDate().toString());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Délégation créée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating delegation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Erreur lors de la création de la délégation"));
        }
    }

    @GetMapping("/own")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public List<DocumentDto> getDocuments(Principal principal) {
        String username = principal.getName();
        logger.info("Fetching documents for user with username: {}", username);

        List<DocumentDto> documents = documentService.getAllUserDocuments(username);
        logger.info("Number of documents found: {}", documents.size());

        documents.forEach(doc -> logger.info("Document: {}", doc));

        return documents;
    }

    @GetMapping("/documents-delegated-to-me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<List<DocumentDto>> getAllDocumentsDelegatedToMe(Principal principal) {
        String username = principal.getName();
        logger.info("Fetching documents delegated to user: {}", username);
        List<DocumentDto> documents = delegationService.getAllDocumentsDelegatedToMe(username);
        logger.info("Documents fetched: {}", documents);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/delete-document/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        logger.info("Request to delete document with ID: {}", id);
        Document document = documentService.findById(id);
        if (document == null) {
            logger.error("Document with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        documentService.deleteDocument(id);

        String username = principal.getName();
        String role = userService.getUserRole(username);
        String ip = getClientIpAddress(request);
        activityLogService.logDocumentDeleted(username, role, ip, document.getNomDocument());

        logger.info("Document with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-delegation/{delegationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Void> deleteDelegation(@PathVariable Long delegationId, Principal principal, HttpServletRequest request) {
        logger.info("Request to delete delegation with ID: {}", delegationId);
        delegationService.deleteDelegationById(delegationId);

        String username = principal.getName();
        String role = userService.getUserRole(username);
        String ip = getClientIpAddress(request);
        activityLogService.logDelegationDeleted(username, role, ip, delegationId);

        logger.info("Delegation with ID {} deleted", delegationId);
        return ResponseEntity.noContent().build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
            ipAddress = "127.0.0.1";
        }

        return ipAddress;
    }
}
