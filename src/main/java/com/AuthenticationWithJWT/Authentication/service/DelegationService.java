package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.dto.DocumentDto;
import com.AuthenticationWithJWT.Authentication.entities.Delegation;
import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.repository.DelegationRepository;
import com.AuthenticationWithJWT.Authentication.repository.DocumentRepository;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DelegationService {
    @Autowired
    private DelegationRepository delegationRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DelegationService.class);

    public void createDelegation(String delegatorUsername, String delegateUsername, LocalDate startDate, LocalDate endDate) {
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
        delegationRepository.save(delegation);
    }

    public List<DocumentDto> getAllDocumentsDelegatedToMe(String username) {
        User user = userRepository.findByMatricule(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        logger.debug("User found: {}", user);

        LocalDate now = LocalDate.now();
        logger.debug("Current date: {}", now);

        List<Delegation> delegations = delegationRepository.findByDelegateAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, now, now);
        logger.debug("Delegations found: {}", delegations);

        List<DocumentDto> documentDtos = new ArrayList<>();
        for (Delegation delegation : delegations) {
            logger.debug("Processing delegation: {}", delegation);

            List<Document> documents = documentRepository.findByUser(delegation.getDelegator());
            logger.debug("Documents found for delegator {}: {}", delegation.getDelegator().getMatricule(), documents);

            for (Document document : documents) {
                logger.debug("Processing document: {}", document);

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
                dto.setDelegateUsername(delegation.getDelegate().getMatricule());
                documentDtos.add(dto);
            }
        }
        logger.debug("Final DocumentDtos: {}", documentDtos);
        return documentDtos;
    }
}