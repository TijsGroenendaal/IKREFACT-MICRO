package nl.hetckm.base.dao;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.helper.CookieHelper;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.model.bouncer.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailsDAO {

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${USER_DETAILS_SERVICE_PORT}")
    private String userDetailsPort;

    private final CookieHelper cookieHelper;
    private final JwtHelper jwtHelper;

    private final RestTemplate restTemplate;

    @Autowired
    public UserDetailsDAO(CookieHelper cookieHelper, JwtHelper jwtHelper) {
        this.cookieHelper = cookieHelper;
        this.jwtHelper = jwtHelper;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new HttpClientErrorHandler());
    }

    public AppUser getUserDetails(String username) {
        final Map<String, Object> authorities = new HashMap<>();
        authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(cookieName, cookieHelper.createCookie(jwtHelper.createJwtForClaims(
                "service",
                authorities
        ), 10).toString());

        final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return restTemplate.exchange(
                "http://user-service:"+ userDetailsPort +"/user/username=" + username,
                HttpMethod.GET,
                entity,
                AppUser.class
        ).getBody();
    }

}
