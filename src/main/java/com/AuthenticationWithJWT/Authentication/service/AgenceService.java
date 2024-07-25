package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.entities.Agence;
import com.AuthenticationWithJWT.Authentication.repository.AgenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AgenceService {

        @Autowired
        private AgenceRepository agenceRepository;

        public Agence findByCodeAgence(String codeAgence) {
            return agenceRepository.findById(codeAgence).orElse(null);
        }
    }

