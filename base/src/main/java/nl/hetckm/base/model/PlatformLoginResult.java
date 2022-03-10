package nl.hetckm.base.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class PlatformLoginResult {

    @NonNull
    private String token;

}
