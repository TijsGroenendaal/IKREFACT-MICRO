package nl.hetckm.bouncer.user;

import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.exceptions.ForbiddenException;
import nl.hetckm.base.exceptions.NoPlatformSpecifiedException;
import nl.hetckm.base.exceptions.UsernameExistsException;
import nl.hetckm.base.model.AppUser;
import nl.hetckm.base.model.Platform;
import nl.hetckm.base.model.Role;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.helper.Argon2PasswordEncoder;
import nl.hetckm.bouncer.platform.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlatformService platformService;
    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Value("${superuser.username}")
    private String superUserUsername;

    @Value("${superuser.hashed-password}")
    private String superUserPassword;

    @Autowired
    public UserService(UserRepository userRepository,
                       @Lazy PlatformService platformService,
                       Argon2PasswordEncoder argon2PasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.argon2PasswordEncoder = argon2PasswordEncoder;
        this.platformService = platformService;
    }

    public AppUser create(AppUser appUser) {
        isAllowedToCreateUser(appUser.getRole());
        UUID platformId = RelationHelper.getPlatformId();
        if (platformId == null && appUser.getPlatform() == null) {
            throw new NoPlatformSpecifiedException();
        } else if (platformId == null) {
            platformId = appUser.getPlatform().getId();
        }
        Platform platform = platformService.findOne(platformId);
        appUser.setPlatform(platform);
        String password = appUser.getPassword();
        appUser.setPassword(hashPassword(password));

        if (appUser.getUsername().equals(superUserUsername)) {
            throw new UsernameExistsException("This username is already taken");
        }

        if (userRepository.existsByUsername(appUser.getUsername())) {
            throw new UsernameExistsException("This username is already taken.");
        }

        return userRepository.save(appUser);
    }

    public Page<AppUser> findAll(Pageable pageable) {
        Page<AppUser> users;
        if (getUserRole() == Role.SUPERUSER) {
            return userRepository.findAll(pageable);
        }
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        users = userRepository.findByPlatform(platform, pageable);
        String requestingUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AppUser> filteredUserResponse = users.get()
                .filter(userResponse -> !userResponse.getUsername().equals(requestingUsername))
                .collect(Collectors.toList());
        return new PageImpl<>(filteredUserResponse, pageable, users.getTotalElements());
    }

    public AppUser findOne(UUID id) {
         AppUser user = userRepository
                 .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AppUser.class));

         if (getUserRole() != Role.SUPERUSER) {
             RelationHelper.isFromParent(user.getPlatform().getId(), RelationHelper.getPlatformId(), AppUser.class);
         }

        return user;
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

    public AppUser update(UUID id, AppUser appUser) {
        AppUser existingAppUser = userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AppUser.class));

        existingAppUser.setEnabled(appUser.isEnabled());

        if (getUserRole() != Role.SUPERUSER) {
            RelationHelper.isFromParent(existingAppUser.getPlatform().getId(), RelationHelper.getPlatformId(), AppUser.class);
        }

        if (appUser.getUsername() != null) {
            existingAppUser.setUsername(appUser.getUsername());
        }
        if (appUser.getPassword() != null)  {
            existingAppUser.setPassword(hashPassword(appUser.getPassword()));

        }
        if (appUser.getRole() != null) {
            existingAppUser.setRole(appUser.getRole());
        }
        userRepository.save(existingAppUser);
        return existingAppUser;
    }

    public void delete(UUID id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(AppUser.class));

        if (getUserRole() != Role.SUPERUSER) {
            RelationHelper.isFromParent(user.getPlatform().getId(), RelationHelper.getPlatformId(), AppUser.class);
        }

        userRepository.delete(user);
    }

    private void isAllowedToCreateUser(Role toCreate) {
        Role userRole = getUserRole();

        if (userRole == Role.ADMIN && toCreate != Role.MODERATOR || userRole == Role.SUPERUSER &&  toCreate != Role.ADMIN) {
            throw new ForbiddenException("Not allowed to create a user with this role.");
        } else if (userRole != Role.SUPERUSER && userRole != Role.ADMIN) {
            throw new ForbiddenException("Not allowed to create a user with this role.");
        }
    }

    private Role getUserRole() {
        List<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().
                getAuthorities().stream().toList();
        try {
            return Role.valueOf(authorities.get(0).toString());
        } catch (IllegalArgumentException e) {
            if (authorities.size() != 1) {
                return Role.valueOf(authorities.get(1).toString());
            } else {
                return null;
            }
        }
    }

    private String hashPassword(String password) {
        return argon2PasswordEncoder.encode(password);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
