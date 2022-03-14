package nl.hetckm.base.model.bouncer;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PlatformLogin {

    @NotEmpty
    String apiKey;

}
