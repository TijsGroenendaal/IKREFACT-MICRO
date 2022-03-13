package nl.hetckm.security;

import io.jsonwebtoken.ClaimJwtException;
import nl.hetckm.base.enums.Role;
import nl.hetckm.base.helper.CookieHelper;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final CookieHelper cookieHelper;
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${userdetails.port}")
    private String userDetailsPort;

    @Autowired
    public TokenAuthenticationFilter(
            JwtHelper jwtHelper,
            CookieHelper cookieHelper
    ) {
        this.jwtHelper = jwtHelper;
        this.cookieHelper = cookieHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtToken(httpServletRequest);
            if (StringUtils.hasText(jwt)) {
                Jwt decodedToken;
                try {
                    decodedToken = jwtHelper.decode(jwt);
                } catch (ClaimJwtException e){
                   HttpCookie httpCookie = cookieHelper.createCookie("", 0);
                   Cookie cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());
                   httpServletResponse.addCookie(cookie);
                   httpServletResponse.setStatus(401);
                   filterChain.doFilter(httpServletRequest, httpServletResponse);
                   return;
                }
                String username = decodedToken.getSubject();

                final Map<String, Object> authorities = new HashMap<>();
                authorities.put(HttpSecurityConfig.AUTHORITIES_CLAIM_NAME, Role.SERVICE);

                final HttpHeaders headers = new HttpHeaders();
                headers.add(cookieName, cookieHelper.createCookie(jwtHelper.createJwtForClaims(
                        "service",
                        authorities
                ), 10).toString());

                final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
                AppUser appUser = new RestTemplate().exchange(
                        "http://user-service:"+ userDetailsPort +"/user/username=" + username,
                        HttpMethod.GET,
                        entity,
                        AppUser.class
                ).getBody();

                String[] userAuthorities = { appUser.getRole().toString() };
                if (appUser.getPlatform() != null) {
                    userAuthorities = new String[]{appUser.getRole().toString(), appUser.getPlatform().getId().toString()};
                }

                final UserDetails userDetails = User.builder()
                                .username(appUser.getUsername())
                                .password(appUser.getPassword())
                                .disabled(!appUser.isEnabled())
                                .authorities(userAuthorities)
                                .build();
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String getJwtToken(HttpServletRequest request) {
        String tokenFromCookie = getJwtFromCookie(request);
        if (tokenFromCookie == null) {
            return getJwtFromRequest(request);
        } else {
            return tokenFromCookie;
        }
    }
}
