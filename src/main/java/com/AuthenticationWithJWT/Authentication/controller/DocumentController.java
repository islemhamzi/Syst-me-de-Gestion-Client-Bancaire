package com.AuthenticationWithJWT.Authentication.controller;

import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.service.DocumentService;
import com.AuthenticationWithJWT.Authentication.service.EmailService;
import com.AuthenticationWithJWT.Authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final UserService userService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    @GetMapping("/own")

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'CHEF_AGENCE', 'TFJO')")
    public List<DocumentDto> getDocuments(Principal principal) {
        String matricule = principal.getName();
        logger.info("Fetching documents for user with matricule: {}", matricule);

        String codeAgence = userService.getCodeAgenceByMatricule(matricule);
        logger.info("Fetched codeAgence: {}", codeAgence);

        List<DocumentDto> documents = documentService.getDocumentsByAgence(codeAgence);
        logger.info("Number of documents found: {}", documents.size());

        documents.forEach(doc -> logger.info("Document: {}", doc));

        return documents;
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
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

        logger.info("Document with ID {} found and ready for download", id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getNomDocument() + "\"")
                .body(resource);
    }
    @PostMapping("/send/{id}")
    public ResponseEntity<Map<String, String>> sendDocumentByEmail(@PathVariable Long id, @RequestParam String email) {
        Document document = documentService.findById(id);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        String attachmentPath = "src/main/documents/" + document.getCheminDocument();
        emailService.sendEmailWithAttachment(email, "Document: " + document.getNomDocument(),
                "Please find the document attached.", attachmentPath);

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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
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
