package com.AuthenticationWithJWT.Authentication.ldap;

import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.enums.Role;
import com.AuthenticationWithJWT.Authentication.exception.EmailNotFoundException;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class CustomUserDetailsContextMapper implements UserDetailsContextMapper {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        String email = ctx.getStringAttribute("mail");
        String firstName = ctx.getStringAttribute("cn");  // LDAP attribute for first name
        String lastName = ctx.getStringAttribute("sn");          // LDAP attribute for last name

        if (email == null || email.isEmpty()) {
            throw new EmailNotFoundException("Email address is missing in LDAP");
        }

        Optional<User> existingUser = userRepository.findByMatricule(username);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update existing user's details
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userRepository.save(user);
        } else {
            // Create new user if not found
            user = new User();
            user.setMatricule(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRoles("USER"); // Assuming Role.USER as the default role
            userRepository.save(user);
        }

        return new CustomUserDetails(username, "", authorities, email);
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // Implement if you need to update LDAP entries from your application
        throw new UnsupportedOperationException("LDAP update not supported.");
    }
}
