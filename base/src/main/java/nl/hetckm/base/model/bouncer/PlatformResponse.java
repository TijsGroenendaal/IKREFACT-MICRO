package nl.hetckm.base.model.bouncer;

import lombok.Value;

import java.util.UUID;

@Value
public class PlatformResponse {

    UUID id;
    String name;
    String apiKey;

    public PlatformResponse(Platform platform) {
        this.id = platform.getId();
        this.name = platform.getName();
        this.apiKey = platform.getApiKey();
    }

}
