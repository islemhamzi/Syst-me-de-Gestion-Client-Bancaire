package com.AuthenticationWithJWT.Authentication.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "evuti")
public class Evuti {
    @Id
    private Long id;

    @Column(name = "matricule_ldap", unique = true, nullable = false)
    private String matriculeLdap;
    @Column(name = "cuti")
    private String matriculeAmplitude;
    @OneToOne(mappedBy = "evuti")
    private User user;
    @ManyToOne
    @JoinColumn(name = "agence_code", referencedColumnName = "code_agence")
    private Agence agence;
}
