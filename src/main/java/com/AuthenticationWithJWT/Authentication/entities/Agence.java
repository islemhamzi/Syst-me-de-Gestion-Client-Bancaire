package com.AuthenticationWithJWT.Authentication.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "agence")
public class Agence {
    @Id

    @Column(name = "code_agence")
    private String codeAgence; // Code agence as ID

    @Column(name = "libelle_agence")
    private String libelleAgence;

}
