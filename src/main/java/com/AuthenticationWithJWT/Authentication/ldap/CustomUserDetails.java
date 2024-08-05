package com.AuthenticationWithJWT.Authentication.ldap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {
    private String email;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String email) {
        super(username, password, authorities);
        this.email = email;
    }

<<<<<<< HEAD
}


=======
    // Ajoutez d'autres méthodes si nécessaire
}

>>>>>>> 127952bcb37bdb6e9c0530f89c2bcc37c1e23762
