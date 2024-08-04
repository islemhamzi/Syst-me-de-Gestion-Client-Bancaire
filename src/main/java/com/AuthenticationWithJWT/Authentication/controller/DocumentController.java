package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.entities.IpAddressUtil;
import com.AuthenticationWithJWT.Authentication.service.ActivityLogService;
import com.AuthenticationWithJWT.Authentication.service.DocumentService;
import com.AuthenticationWithJWT.Authentication.service.EmailService;
import com.AuthenticationWithJWT.Authentication.service.UserService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final UserService userService;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public List<DocumentDto> getAllDocuments(Principal principal, HttpServletRequest request) {
        String matricule = principal.getName();
        String role = userService.getUserRole(matricule);
        String ip = IpAddressUtil.getClientIp(request);
        logger.info("Fetching all documents for user with matricule: {}", matricule);

        activityLogService.logDocumentsViewed(matricule, role, ip);

        return documentService.getAllUserDocuments(matricule);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        logger.info("Request to download document with ID: {}", id);
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

        String username = principal.getName();
        String role = userService.getUserRole(username);
        String ip = IpAddressUtil.getClientIp(request);
        activityLogService.logDocumentDownloaded(username, role, ip, document.getNomDocument());

        logger.info("Document with ID {} found and ready for download", id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getNomDocument() + "\"")
                .body(resource);
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<Map<String, String>> sendDocumentByEmail(
            @PathVariable Long id,
            @RequestParam String email,
            Principal principal,
            HttpServletRequest request) {
        Document document = documentService.findById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        String attachmentPath = "src/main/documents/" + document.getCheminDocument();
        emailService.sendEmailWithAttachment(email, "Document: " + document.getNomDocument(),
                "Please find the document attached.", attachmentPath);

        String username = principal.getName();
        String role = userService.getUserRole(username);
        String ip = IpAddressUtil.getClientIp(request);
        activityLogService.logEmailSent(username, role, ip, email, document.getNomDocument());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email sent successfully to " + email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long id) {
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
        String ip = IpAddressUtil.getClientIp(request);
        activityLogService.logDocumentDeleted(username, role, ip, document.getNomDocument());

        logger.info("Document with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-delegation/{delegationId}")
    public ResponseEntity<Void> deleteDelegation(@PathVariable Long delegationId, Principal principal, HttpServletRequest request) {
        logger.info("Request to delete delegation with ID: {}", delegationId);
        documentService.deleteDelegationById(delegationId);

        String username = principal.getName();
        String role = userService.getUserRole(username);
        String ip = IpAddressUtil.getClientIp(request);
        activityLogService.logDelegationDeleted(username, role, ip, delegationId);

        logger.info("Delegation with ID {} deleted", delegationId);
        return ResponseEntity.noContent().build();
    }
}
