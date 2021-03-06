package nl.hetckm.base.model.bouncer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter @Setter
public class NewPlatformResponse {
    private UUID id;
    private String name;
    private String apiKey;
    private UserResponse user;

    public NewPlatformResponse(Platform platform, UserResponse userResponse) {
        this.id = platform.getId();
        this.name = platform.getName();
        this.apiKey = platform.getApiKey();
        this.user = userResponse;
    }
}
