package com.AuthenticationWithJWT.Authentication.service;

import com.AuthenticationWithJWT.Authentication.entities.Evuti;
import com.AuthenticationWithJWT.Authentication.repository.EvutiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvutiService {

    @Autowired
    private EvutiRepository evutiRepository;

    public Evuti findByMatriculeLdap(String matriculeLdap) {
        return evutiRepository.findById(Long.valueOf(matriculeLdap)).orElse(null);
    }

    public void saveEvuti(Evuti evuti) {
        evutiRepository.save(evuti);
    }
}
