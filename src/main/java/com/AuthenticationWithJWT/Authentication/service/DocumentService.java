package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;

    public List<DocumentDto> getDocumentsByAgence(String codeAgence) {
        return documentRepository.findByAgence_CodeAgence(codeAgence).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setIdDocument(document.getIdDocument());
        dto.setNumeroCompte(document.getNumeroCompte());
        dto.setCodeClient(document.getCodeClient());
        dto.setTypeDocument(document.getTypeDocument());
        dto.setDateEdition(document.getDateEdition());
        dto.setDateComptable(document.getDateComptable());
        dto.setNomDocument(document.getNomDocument());
        dto.setCheminDocument(document.getCheminDocument());
        dto.setAgenceCode(document.getAgence().getCodeAgence());
        dto.setUserMatricule(document.getUser().getMatricule());
        dto.setUserEmail(document.getUser().getEmail());

        if (document.getAgenceEdition() != null) {
            dto.setAgenceEdition(document.getAgenceEdition().getCodeAgence());
        } else {
            dto.setAgenceEdition(null);
        }

        return dto;
    }

    public Document findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
