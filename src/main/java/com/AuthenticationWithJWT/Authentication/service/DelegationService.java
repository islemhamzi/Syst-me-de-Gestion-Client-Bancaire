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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DelegationService {
    private final DelegationRepository delegationRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DelegationService.class);

    public void createDelegation(String delegatorUsername, String delegateUsername, LocalDate startDate, LocalDate endDate, Long documentId) {
        User delegator = userRepository.findByMatricule(delegatorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Delegator not found: " + delegatorUsername));
        User delegate = userRepository.findByMatricule(delegateUsername)
                .orElseThrow(() -> new IllegalArgumentException("Delegate not found: " + delegateUsername));
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        Delegation delegation = new Delegation();
        delegation.setDelegator(delegator);
        delegation.setDelegate(delegate);
        delegation.setStartDate(startDate);
        delegation.setEndDate(endDate);
        delegation.setDocumentId(documentId); // Assurez-vous de bien définir le documentId
        delegationRepository.save(delegation);
    }


    public List<DocumentDto> getAllDocumentsDelegatedToMe(String username) {
        User user = userRepository.findByMatricule(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        LocalDate now = LocalDate.now();

        List<Delegation> delegations = delegationRepository.findByDelegateAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, now, now);

        List<DocumentDto> documentDtos = new ArrayList<>();
        for (Delegation delegation : delegations) {
            List<Document> documents = documentRepository.findByUser(delegation.getDelegator());
            for (Document document : documents) {
                DocumentDto dto = new DocumentDto();
                dto.setIdDocument(document.getIdDocument());
                dto.setNumeroCompte(document.getNumeroCompte());
                dto.setCodeClient(document.getCodeClient());
                dto.setTypeDocument(document.getTypeDocument());
                dto.setDateEdition(document.getDateEdition());
                dto.setDateComptable(document.getDateComptable());
                dto.setNomDocument(document.getNomDocument());
                dto.setCheminDocument(document.getCheminDocument());
                dto.setUserMatricule(delegation.getDelegator().getMatricule());
                dto.setUserEmail(delegation.getDelegator().getEmail());
                dto.setAgenceCode(document.getAgence().getCodeAgence());
                dto.setAgenceEdition(document.getAgenceEdition().getLibelleAgence());
                dto.setDelegatorUsername(delegation.getDelegator().getMatricule());
                dto.setStartDate(delegation.getStartDate());   // Set start date
                dto.setEndDate(delegation.getEndDate());       // Set end date
                dto.setDelegationId(delegation.getId());       // Set delegation ID
                documentDtos.add(dto);
            }
        }
        return documentDtos;
    }

    public void deleteDelegationById(Long delegationId) {
        Delegation delegation = delegationRepository.findById(delegationId).orElseThrow(() -> new IllegalArgumentException("Delegation not found: " + delegationId));
        delegationRepository.delete(delegation);
        logger.info("Delegation with ID {} deleted", delegationId);
    }

}
