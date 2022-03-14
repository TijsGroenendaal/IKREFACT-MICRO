package nl.hetckm.base.model.bouncer;

import lombok.Data;
import lombok.NonNull;

@Data
public class PlatformLoginResult {

    @NonNull
    private String token;

}
