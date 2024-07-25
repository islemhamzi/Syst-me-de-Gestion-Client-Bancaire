package com.AuthenticationWithJWT.Authentication.entities;

import com.AuthenticationWithJWT.Authentication.enums.AccountStatus;
import com.AuthenticationWithJWT.Authentication.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "matricule", unique = true, nullable = false)
    private String matricule; //c'est le matricule ldap

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    private String phoneNumber ;
    private String password;

    @Column(name = "roles")
    private String roles;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricule", referencedColumnName = "matricule_ldap", insertable = false, updatable = false)
    private Evuti evuti;

    @Column(name = "creation_date", updatable = false)
    private Instant creationDate = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(roles.split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return matricule;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}
