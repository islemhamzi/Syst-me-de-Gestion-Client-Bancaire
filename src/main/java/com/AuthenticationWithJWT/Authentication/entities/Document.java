package com.AuthenticationWithJWT.Authentication.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "document")
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    @Column(nullable = false)
    private String numeroCompte;

    @Column(nullable = false)
    private String codeClient;

    @ManyToOne
    @JoinColumn(name = "agence_code", referencedColumnName = "code_agence")
    private Agence agence;

    @Column(nullable = false)
    private String typeDocument;

    @Column(nullable = false)
    private LocalDate dateEdition;

    @Column(nullable = false)
    private LocalDate dateComptable;

    @Column(nullable = false)
    private String nomDocument;

    @Column(nullable = false)
    private String cheminDocument;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "agence_edition_code", referencedColumnName = "code_agence")
    private Agence agenceEdition;

    @Column(nullable = false)
    private boolean consulted; // Add this line

    @Column(nullable = false)
    private boolean emailed; // Add this line
}
