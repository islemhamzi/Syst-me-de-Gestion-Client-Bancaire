package com.AuthenticationWithJWT.Authentication.payload.request;

import com.AuthenticationWithJWT.Authentication.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    private String matricule;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private List<Role> role;
    private String ldapMatricule;  // New field for LDAP matricule

    // Existing getters and setters

    public String getLdapMatricule() {
        return ldapMatricule;  // Getter for the new field
    }

    // Optionally add a setter if needed
    public void setLdapMatricule(String ldapMatricule) {
        this.ldapMatricule = ldapMatricule;
    }
}
