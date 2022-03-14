package nl.hetckm.base.model.bouncer;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.enums.ChallengeStatus;

@Getter @Setter
public class ChallengeUpdate {
    ChallengeStatus status;
}
