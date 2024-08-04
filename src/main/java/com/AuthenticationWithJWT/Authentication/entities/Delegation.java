package com.AuthenticationWithJWT.Authentication.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "delegation")
public class Delegation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delegator_id", referencedColumnName = "id")
    @JsonBackReference
    private User delegator;

    @ManyToOne
    @JoinColumn(name = "delegate_id", referencedColumnName = "id")
    @JsonManagedReference
    private User delegate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Long documentId;


}
