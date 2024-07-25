package com.AuthenticationWithJWT.Authentication.repository;

import com.AuthenticationWithJWT.Authentication.entities.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenceRepository extends JpaRepository<Agence, String> {
    Agence findByCodeAgence(String codeAgence);
}
