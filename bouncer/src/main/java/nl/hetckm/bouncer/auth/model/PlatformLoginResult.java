package nl.hetckm.bouncer.auth.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class PlatformLoginResult {

    @NonNull
    private String token;

}
