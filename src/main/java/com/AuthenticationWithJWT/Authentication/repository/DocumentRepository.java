package com.AuthenticationWithJWT.Authentication.repository;

import com.AuthenticationWithJWT.Authentication.entities.Document;
import com.AuthenticationWithJWT.Authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByAgence_CodeAgence(String codeAgence);
    List<Document> findByUser(User user);

    @Query("SELECT d.typeDocument, COUNT(d) FROM Document d GROUP BY d.typeDocument")
    List<Object[]> countDocumentsByType();

    @Query("SELECT d.typeDocument, COUNT(d) FROM Document d WHERE d.consulted = true GROUP BY d.typeDocument")
    List<Object[]> countDocumentsConsultedByType();

    @Query("SELECT d.typeDocument, COUNT(d) FROM Document d WHERE d.emailed = true GROUP BY d.typeDocument")
    List<Object[]> countDocumentsEmailedByType();

    @Query("SELECT d.typeDocument, d.agence.codeAgence, COUNT(d) FROM Document d GROUP BY d.typeDocument, d.agence.codeAgence")
    List<Object[]> countDocumentsByTypeAndAgency();
}

