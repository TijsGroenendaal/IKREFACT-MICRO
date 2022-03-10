package nl.hetckm.bouncer.verdict.model;

import lombok.Data;

import java.util.UUID;

@Data
public class VerdictResponse {
    UUID id;
    boolean approved;
    String reason;

    public VerdictResponse(Verdict verdict) {
        this.approved = verdict.getApproved();
        this.id = verdict.getId();
        this.reason = verdict.getReason();
    }

}
