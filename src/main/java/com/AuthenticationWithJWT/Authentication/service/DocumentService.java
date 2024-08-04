package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Delegation;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.repository.DelegationRepository;
import com.AuthenticationWithJWT.Authentication.repository.DocumentRepository;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DelegationRepository delegationRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public List<DocumentDto> getDocumentsByAgence(String codeAgence) {
        return documentRepository.findByAgence_CodeAgence(codeAgence).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DocumentDto> getAllUserDocuments(String username) {
        User user = userRepository.findByMatricule(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<Document> userDocuments = documentRepository.findByUser(user);
        List<DocumentDto> documentDtos = userDocuments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        LocalDate now = LocalDate.now();
        List<Delegation> delegations = delegationRepository.findByDelegateAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, now, now);

        for (Delegation delegation : delegations) {
            List<Document> delegatedDocuments = documentRepository.findByUser(delegation.getDelegator());
            for (Document document : delegatedDocuments) {
                DocumentDto dto = convertToDto(document);
                dto.setDelegatorUsername(delegation.getDelegator().getMatricule());
                dto.setStartDate(delegation.getStartDate());
                dto.setEndDate(delegation.getEndDate());
                dto.setDelegationId(delegation.getId());  // Set delegation ID
                documentDtos.add(dto);
            }
        }
        return documentDtos;
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

    public void deleteDelegationById(Long delegationId) {
        delegationRepository.deleteById(delegationId);
    }

}
