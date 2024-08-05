<<<<<<< HEAD
package com.AuthenticationWithJWT.Authentication.ldap;

import com.AuthenticationWithJWT.Authentication.entities.User;
import com.AuthenticationWithJWT.Authentication.enums.Role;
import com.AuthenticationWithJWT.Authentication.exception.EmailNotFoundException;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
=======
import com.AuthenticationWithJWT.Authentication.ldap.CustomUserDetails;
>>>>>>> 127952bcb37bdb6e9c0530f89c2bcc37c1e23762
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;
<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Autowired;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import com.AuthenticationWithJWT.Authentication.entities.User;
>>>>>>> 127952bcb37bdb6e9c0530f89c2bcc37c1e23762

import java.util.Collection;
import java.util.Optional;

@Component
public class CustomUserDetailsContextMapper implements UserDetailsContextMapper {

<<<<<<< HEAD
    @Autowired
    private UserRepository userRepository;
=======
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsContextMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
>>>>>>> 127952bcb37bdb6e9c0530f89c2bcc37c1e23762

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        String email = ctx.getStringAttribute("mail");
<<<<<<< HEAD
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

=======
        // Vérifiez l'existence de l'utilisateur dans votre base de données
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            // Créez et inscrivez l'utilisateur si non trouvé
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setMatricule(username);
            // Définissez les autres propriétés de newUser comme nécessaire
            userRepository.save(newUser);
        }

        // Procédez comme d'habitude pour charger ou créer UserDetails
>>>>>>> 127952bcb37bdb6e9c0530f89c2bcc37c1e23762
        return new CustomUserDetails(username, "", authorities, email);
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
<<<<<<< HEAD
        // Implement if you need to update LDAP entries from your application
        throw new UnsupportedOperationException("LDAP update not supported.");
=======
        // Méthode pour les scénarios de mise à jour, généralement non utilisée lors de l'authentification
>>>>>>> 127952bcb37bdb6e9c0530f89c2bcc37c1e23762
    }
}
