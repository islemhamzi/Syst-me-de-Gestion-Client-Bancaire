package com.AuthenticationWithJWT.Authentication.repository;

import com.AuthenticationWithJWT.Authentication.entities.Evuti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvutiRepository extends JpaRepository<Evuti, Long> {
    Optional<Evuti> findByMatriculeLdap(String matriculeLdap);
}

