package nl.hetckm.base.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PlatformLogin {

    @NotEmpty
    String apiKey;

}
