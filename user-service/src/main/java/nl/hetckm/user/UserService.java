package nl.hetckm.user;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Value("${superuser.username}")
    private String superUserUsername;

    @Value("${superuser.hashed-password}")
    private String superUserPassword;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser findOneByUsername(String username) {
        if (username.equals(superUserUsername)) {
            AppUser user = new AppUser();
            user.setRole(Role.SUPERUSER);
            user.setUsername(superUserUsername);
            user.setPassword(new String(Base64.getDecoder().decode(superUserPassword)));
            user.setPlatform(null);
            user.setEnabled(true);
            return user;
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(AppUser.class));
    }
}
