package com.AuthenticationWithJWT.Authentication.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "activity_log")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Instant date;

    @Column(nullable = false)
    private String username; // Changed from user to username

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String ip;
}
