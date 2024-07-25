package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.dto.DelegationRequest;
import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.service.DelegationService;
import com.AuthenticationWithJWT.Authentication.service.DocumentService;
import com.AuthenticationWithJWT.Authentication.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DelegationController {

    private static final Logger logger = LoggerFactory.getLogger(DelegationController.class);

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/delegations/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Map<String, String>> createDelegation(@RequestBody DelegationRequest delegationRequest) {
        try {
            delegationService.createDelegation(delegationRequest.getDelegatorUsername(), delegationRequest.getDelegateUsername(), delegationRequest.getStartDate(), delegationRequest.getEndDate());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Delegation created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating delegation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error creating delegation"));
        }
    }


    @GetMapping("/delegations/documents-delegated-to-me")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<List<DocumentDto>> getAllDocumentsDelegatedToMe(@org.jetbrains.annotations.NotNull Principal principal) {
        String username = principal.getName();
        logger.info("Fetching documents delegated to user: {}", username);
        List<DocumentDto> documents = delegationService.getAllDocumentsDelegatedToMe(username);
        logger.info("Documents fetched: {}", documents);
        return ResponseEntity.ok(documents);
    }




    @GetMapping("/delegations/download/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Resource> downloadDelegatedDocument(@PathVariable Long id) {
        Document document = documentService.findById(id);
        if (document == null) {
            logger.error("Document with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get("src/main/documents/" + document.getCheminDocument());
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.error("Document with ID {} is not readable or does not exist at path: {}", id, path);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error while constructing URL for document with ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        logger.info("Document with ID {} found and ready for download", id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getNomDocument() + "\"")
                .body(resource);
    }

    @PostMapping("/delegations/send/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Map<String, String>> sendDelegatedDocumentByEmail(@PathVariable Long id, @RequestParam String email) {
        logger.info("Request to send document with ID {} by email to {}", id, email);
        Document document = documentService.findById(id);
        if (document == null) {
            logger.error("Document with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        String attachmentPath = "src/main/documents/" + document.getCheminDocument();
        try {
            emailService.sendEmailWithAttachment(email, "Document: " + document.getNomDocument(),
                    "Please find the document attached.", attachmentPath);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Email sent successfully to " + email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to send email with attachment to {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error sending email"));
        }
    }

    @GetMapping("/delegations/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Resource> viewDelegatedDocument(@PathVariable Long id) {
        logger.info("Request to view document with ID: {}", id);
        Document document = documentService.findById(id);
        if (document == null) {
            logger.error("Document with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get("src/main/documents/" + document.getCheminDocument());
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.error("Document with ID {} is not readable or does not exist at path: {}", id, path);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error while constructing URL for document with ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        logger.info("Document with ID {} found and ready for viewing", id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @DeleteMapping("/delegations/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public ResponseEntity<Void> deleteDelegatedDocument(@PathVariable Long id) {
        logger.info("Request to delete document with ID: {}", id);
        Document document = documentService.findById(id);
        if (document == null) {
            logger.error("Document with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        documentService.deleteDocument(id);
        logger.info("Document with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

}
