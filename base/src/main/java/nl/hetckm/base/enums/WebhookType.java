package nl.hetckm.base.enums;

import lombok.Getter;
import nl.hetckm.base.interfaces.WebhookTestable;
import nl.hetckm.base.model.Challenge;
import nl.hetckm.base.model.Verdict;
import nl.hetckm.base.model.Verification;

/**
 * The enum Webhook type, specifies which entity was changed.
 */
public enum WebhookType {
    /**
     * Verdict webhook type.
     */
    VERDICT(Verdict::new),
    /**
     * Verification webhook type.
     */
    VERIFICATION(Verification::new),
    /**
     * Challenge webhook type.
     */
    CHALLENGE(Challenge::new);

    /**
     * The Value.
     */
    @Getter
    private final WebhookTestable testable;

    WebhookType(WebhookTestable testable) {
        this.testable = testable;
    }
}
