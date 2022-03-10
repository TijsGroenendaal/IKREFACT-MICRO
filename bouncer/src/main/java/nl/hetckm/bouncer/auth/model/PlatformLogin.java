package nl.hetckm.bouncer.auth.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PlatformLogin {

    @NotEmpty
    String apiKey;

}
