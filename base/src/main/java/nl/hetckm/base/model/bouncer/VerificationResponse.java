package nl.hetckm.base.model.bouncer;

import lombok.Data;
import nl.hetckm.base.enums.VerificationStatus;

import java.util.Date;
import java.util.UUID;

@Data
public class VerificationResponse {
    UUID id;
    Date createDate;
    VerificationStatus status;
    String name;
    int maxChallengeLifetime;

    public VerificationResponse(Verification verification) {
        this.id = verification.getId();
        this.name = verification.getName();
        this.createDate = verification.getCreateDate();
        this.status = verification.getStatus();
        this.maxChallengeLifetime = verification.getMaxChallengeLifetime();
    }
}
