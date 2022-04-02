package nl.hetckm.base.dao;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.helper.RelationHelper;
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

    @Value("${PRESET_SERVICE_PORT}")
    private String presetServicePort;

    private final JwtHelper jwtHelper;

    private final RestTemplate restTemplate;

    @Autowired
    public PresetDAO(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new HttpClientErrorHandler());
    }

    public Preset getOne(UUID presetId) {
        final Map<String, Object> authorities = new HashMap<>();
        authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE + " " + RelationHelper.getPlatformId());

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtHelper.createJwtForClaims(
                "service",
                authorities)
        );

        final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return restTemplate.exchange(
                "http://preset-service:"+ presetServicePort +"/preset/" + presetId,
                HttpMethod.GET,
                entity,
                Preset.class
        ).getBody();
    }

    public void deleteAllByPlatform(UUID platformId) {
        final Map<String, Object> authorities = new HashMap<>();
        authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE + " " + (
                RelationHelper.getPlatformId() == null ? "" : RelationHelper.getPlatformId()));

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtHelper.createJwtForClaims(
                "service", authorities
        ));

        final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        restTemplate.exchange(
                "http://preset-service:"+ presetServicePort +"/preset/platform/" + platformId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }

}
