package nl.hetckm.security;

import io.jsonwebtoken.ClaimJwtException;
import nl.hetckm.base.enums.Role;
import nl.hetckm.base.helper.CookieHelper;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.helper.RelationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final CookieHelper cookieHelper;
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

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


                final UsernamePasswordAuthenticationToken authentication;
                final Role role =  RelationHelper.getRoleFromJWT(decodedToken, AUTHORITIES_CLAIM_NAME);
                final UUID platformId = RelationHelper.getPlatformIdFromJWT(decodedToken, AUTHORITIES_CLAIM_NAME);

                String[] userAuthorities = { role.toString() };
                if (platformId != null) {
                    userAuthorities = new String[]{ role.toString(), platformId.toString() };
                }

                if (role.equals(Role.PLATFORM)) {

                    final UserDetails userDetails = User.builder()
                            .username("PLATFORM")
                            .password("")
                            .authorities(userAuthorities)
                            .build();

                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                } else if (role.equals(Role.SERVICE)) {

                    final UserDetails userDetails = User.builder()
                            .username("SERVICE")
                            .password("")
                            .authorities(userAuthorities)
                            .build();

                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                } else if (role.equals(Role.SUPERUSER) || role.equals(Role.ADMIN) || role.equals(Role.MODERATOR)) {

                    final UserDetails userDetails = User.builder()
                            .username(decodedToken.getSubject())
                            .password("")
                            .authorities(userAuthorities)
                            .build();

                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                } else {
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                    return;
                }
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
