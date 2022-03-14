package nl.hetckm.bouncer.auth;

import nl.hetckm.base.exceptions.AppUserDisabledException;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.exceptions.WrongCredentialsException;
import nl.hetckm.base.helper.Argon2PasswordEncoder;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.model.bouncer.*;
import nl.hetckm.bouncer.platform.PlatformService;
import nl.hetckm.bouncer.user.UserPrincipalService;
import nl.hetckm.bouncer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

    private final UserPrincipalService userPrincipalService;
    private final PlatformService platformService;
    private final Argon2PasswordEncoder argon2PasswordEncoder;
    private final JwtHelper jwtHelper;
    private final UserService userService;

    @Autowired
    public AuthService(
            @Lazy UserPrincipalService userPrincipalService,
            @Lazy PlatformService platformService,
            @Lazy UserService userService,
            Argon2PasswordEncoder argon2PasswordEncoder,
            JwtHelper jwtHelper
    ) {
        this.userPrincipalService = userPrincipalService;
        this.platformService = platformService;
        this.argon2PasswordEncoder = argon2PasswordEncoder;
        this.jwtHelper = jwtHelper;
        this.userService = userService;
    }

    public UserLoginResult loginUser(UserLogin userLogin) {
        UserDetails userDetails;
        try {
            userDetails = userPrincipalService.loadUserByUsername(userLogin.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new WrongCredentialsException();
        }

        if (!argon2PasswordEncoder.matches(userLogin.getPassword(), userDetails.getPassword())) {
            throw new WrongCredentialsException();
        }

        if (!userDetails.isEnabled()) {
            throw new AppUserDisabledException();
        }

        Map<String, Object> claims = new HashMap<>();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        claims.put(AUTHORITIES_CLAIM_NAME, authorities);

        String jwt = jwtHelper.createJwtForClaims(userLogin.getUsername(), claims);

        AppUser user = this.userService.findOneByUsername(userDetails.getUsername());
        return new UserLoginResult(jwt, new UserResponse(user));
    }

    public PlatformLoginResult loginPlatform(PlatformLogin platformLogin) {
        try {
            Platform platform = platformService.findOneByApiKey(platformLogin.getApiKey());

            Map<String, Object> claims = new HashMap<>();
            claims.put(AUTHORITIES_CLAIM_NAME, "PLATFORM " + platform.getId().toString());

            String jwt = jwtHelper.createJwtForClaims(platform.getId().toString(), claims);
            return new PlatformLoginResult(jwt);
        } catch (EntityNotFoundException e) {
            throw new BadCredentialsException("Invalid API key");
        }
    }

    public UserResponse getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = this.userService.findOneByUsername(username);
        return new UserResponse(user);
    }



}
