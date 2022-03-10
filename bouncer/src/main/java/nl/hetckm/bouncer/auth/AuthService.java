package nl.hetckm.bouncer.auth;

import nl.hetckm.bouncer.auth.model.PlatformLogin;
import nl.hetckm.bouncer.auth.model.PlatformLoginResult;
import nl.hetckm.bouncer.auth.model.UserLogin;
import nl.hetckm.bouncer.auth.model.UserLoginResult;
import nl.hetckm.bouncer.exceptions.AppUserDisabledException;
import nl.hetckm.bouncer.exceptions.EntityNotFoundException;
import nl.hetckm.bouncer.exceptions.WrongCredentialsException;
import nl.hetckm.bouncer.helper.Argon2PasswordEncoder;
import nl.hetckm.bouncer.helper.JwtHelper;
import nl.hetckm.bouncer.platform.PlatformService;
import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.user.UserPrincipalService;
import nl.hetckm.bouncer.user.UserService;
import nl.hetckm.bouncer.user.model.AppUser;
import nl.hetckm.bouncer.user.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
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

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.cookie-secure}")
    private Boolean secureCookie;

    @Value("${jwt.cookie.restrict-site}")
    private Boolean sameSiteStrict;

    private final UserPrincipalService userPrincipalService;
    private final PlatformService platformService;
    private final Argon2PasswordEncoder argon2PasswordEncoder;
    private final JwtHelper jwtHelper;
    private final UserService userService;

    @Autowired
    public AuthService(
            UserPrincipalService userPrincipalService,
            PlatformService platformService,
            Argon2PasswordEncoder argon2PasswordEncoder,
            JwtHelper jwtHelper,
            UserService userService
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
        claims.put(HttpSecurityConfig.AUTHORITIES_CLAIM_NAME, authorities);

        String jwt = jwtHelper.createJwtForClaims(userLogin.getUsername(), claims);

        AppUser user = this.userService.findOneByUsername(userDetails.getUsername());
        return new UserLoginResult(jwt, new UserResponse(user));
    }

    public PlatformLoginResult loginPlatform(PlatformLogin platformLogin) {
        try {
            Platform platform = platformService.findOneByApiKey(platformLogin.getApiKey());

            Map<String, Object> claims = new HashMap<>();
            claims.put(HttpSecurityConfig.AUTHORITIES_CLAIM_NAME, "PLATFORM " + platform.getId().toString());

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

    public HttpCookie createCookie(String value, long maxAge) {
        return ResponseCookie.from(cookieName, value)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(secureCookie)
                .sameSite(sameSiteStrict ? "Strict" : "Lax")
                .build();
    }

}
