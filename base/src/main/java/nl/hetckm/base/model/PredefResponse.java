package nl.hetckm.base.model;

import lombok.Value;

import java.util.UUID;

@Value
public class PredefResponse {

    UUID id;
    String reason;

    public PredefResponse(Predef predef) {
        this.id = predef.getId();
        this.reason = predef.getReason();
    }

}
