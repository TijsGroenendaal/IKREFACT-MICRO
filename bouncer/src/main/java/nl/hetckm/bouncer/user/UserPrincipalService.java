package nl.hetckm.bouncer.user;

import nl.hetckm.base.model.bouncer.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserPrincipalService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserPrincipalService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userService.findOneByUsername(username);
        String[] authorities = { user.getRole().toString() };
        if (user.getPlatform() != null) {
            authorities = new String[]{user.getRole().toString(), user.getPlatform().getId().toString()};
        }
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(authorities)
                .build();

    }

}
