package nl.hetckm.base.helper;

import nl.hetckm.base.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpHelper {

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

    private final JwtHelper jwtHelper;

    @Autowired
    public HttpHelper(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    public <T> HttpEntity<T> createHttpAuthorizationHeader(T body) {
        final Map<String, Object> authorities = new HashMap<>();
        if (RelationHelper.getPlatformId() == null) {
            authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE);
        } else {
            authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE + " " + RelationHelper.getPlatformId());
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtHelper.createJwtForClaims(
                "service",
                authorities
        ));

        return new HttpEntity<>(body, headers);
    }

}
