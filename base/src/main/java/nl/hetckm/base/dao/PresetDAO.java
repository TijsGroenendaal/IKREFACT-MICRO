package nl.hetckm.base.dao;

import nl.hetckm.base.helper.HttpHelper;
import nl.hetckm.base.model.preset.Preset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class PresetDAO {

    @Value("${PRESET_SERVICE_PORT}")
    private String presetServicePort;

    private final RestTemplate restTemplate;

    private final HttpHelper httpHelper;

    @Autowired
    public PresetDAO(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new HttpClientErrorHandler());
    }

    public Preset getOne(UUID presetId) {
        return restTemplate.exchange(
                "http://preset-service:"+ presetServicePort +"/preset/" + presetId,
                HttpMethod.GET,
                httpHelper.createHttpAuthorizationHeader(""),
                Preset.class
        ).getBody();
    }

    public void deleteAllByPlatform(UUID platformId) {
        restTemplate.exchange(
                "http://preset-service:"+ presetServicePort +"/preset/platform/" + platformId,
                HttpMethod.DELETE,
                httpHelper.createHttpAuthorizationHeader(""),
                Void.class
        );
    }

}
