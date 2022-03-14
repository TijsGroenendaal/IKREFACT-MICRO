package nl.hetckm.user;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.model.bouncer.AppUser;
import nl.hetckm.base.model.bouncer.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        final AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(AppUser.class));
        final Platform platform = new Platform();
        platform.setName(appUser.getPlatform().getName());
        platform.setId(appUser.getPlatform().getId());
        appUser.setPlatform(platform);
        return appUser;
    }
}
