package nl.hetckm.base.dao;

import nl.hetckm.base.exceptions.ServiceUnavailableException;
import nl.hetckm.base.helper.HttpHelper;
import nl.hetckm.base.model.preset.Preset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
public class PresetDAO {

    @Value("${PRESET_SERVICE_PORT}")
    private String presetServicePort;

    private final HttpHelper httpHelper;

    private final WebClient webClient;

    @Autowired
    public PresetDAO(HttpHelper httpHelper) {
        this.webClient = WebClient.create();
        this.httpHelper = httpHelper;
    }

    public Preset getOne(UUID presetId) {
        return webClient
                .get()
                .uri("http://preset-service:" + presetServicePort + "/preset/" + presetId)
                .header("Authorization", httpHelper.createHttpAuthorizationHeaderValues())
                .retrieve().bodyToMono(Preset.class).doOnError(mono -> {
                    throw new ServiceUnavailableException("Preset Service");
                }).block();
    }

    public void deleteAllByPlatform(UUID platformId) {
        webClient
                .delete()
                .uri("http://preset-service:"+ presetServicePort +"/preset/platform/" + platformId)
                .header("Authorization", httpHelper.createHttpAuthorizationHeaderValues())
                .retrieve().toBodilessEntity().doOnError(mono -> {
                    // TODO save failed transaction
                }).subscribe();
    }
}
