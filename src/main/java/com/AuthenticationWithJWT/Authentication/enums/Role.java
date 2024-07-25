package com.AuthenticationWithJWT.Authentication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.AuthenticationWithJWT.Authentication.enums.Privilege.*;

@RequiredArgsConstructor
public enum Role {
    ADMIN(
            Set.of(
                    ACTIVATE_ACCOUNT,
                    DEACTIVATE_ACCOUNT,
                    ADD_PROFILE,
                    DELETE_PROFILE,
                    MANAGE_USERS
            )
    ),
    USER(
            Set.of(CONSULT_OWN_DOCUMENTS)
    ),
    CHEF_AGENCE(
            Set.of(CONSULT_TFJO, CONSULT_TRANSACTIONAL)
    ),
    TFJO(
            Set.of(CONSULT_TFJO)
    );

    @Getter
    private final Set<Privilege> privileges;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPrivileges()
                .stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
