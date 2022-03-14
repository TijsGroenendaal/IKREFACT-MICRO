package nl.hetckm.base.dao;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.helper.CookieHelper;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.model.preset.Preset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PresetDAO {

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${PRESET_SERVICE_PORT}")
    private String presetServicePort;

    private final CookieHelper cookieHelper;
    private final JwtHelper jwtHelper;

    @Autowired
    public PresetDAO(CookieHelper cookieHelper, JwtHelper jwtHelper) {
        this.cookieHelper = cookieHelper;
        this.jwtHelper = jwtHelper;
    }

    public Preset getOne(UUID presetId) {
        final Map<String, Object> authorities = new HashMap<>();
        authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(cookieName, cookieHelper.createCookie(jwtHelper.createJwtForClaims(
                "service",
                authorities
        ), 10).toString());

        final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return new RestTemplate().exchange(
                "http://preset-service:"+ presetServicePort +"/preset/" + presetId,
                HttpMethod.GET,
                entity,
                Preset.class
        ).getBody();
    }

    public void deleteAllByPlatform(UUID platformId) {
        final Map<String, Object> authorities = new HashMap<>();
        authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(cookieName, cookieHelper.createCookie(jwtHelper.createJwtForClaims(
                "service",
                authorities
        ), 10).toString());

        final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        new RestTemplate().delete(
                "http://preset-service:"+ presetServicePort +"/preset/platform/" + platformId,
                HttpMethod.DELETE,
                entity
        );
    }

}
