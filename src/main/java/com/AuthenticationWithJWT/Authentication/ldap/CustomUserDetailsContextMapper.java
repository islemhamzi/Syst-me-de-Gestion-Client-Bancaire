import com.AuthenticationWithJWT.Authentication.ldap.CustomUserDetails;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.AuthenticationWithJWT.Authentication.repository.UserRepository;
import com.AuthenticationWithJWT.Authentication.entities.User;

import java.util.Collection;
import java.util.Optional;

@Component
public class CustomUserDetailsContextMapper implements UserDetailsContextMapper {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsContextMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        String email = ctx.getStringAttribute("mail");
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
        return new CustomUserDetails(username, "", authorities, email);
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // Méthode pour les scénarios de mise à jour, généralement non utilisée lors de l'authentification
    }
}
