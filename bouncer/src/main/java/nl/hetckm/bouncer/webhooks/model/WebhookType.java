package nl.hetckm.bouncer.webhooks.model;

import lombok.Getter;
import nl.hetckm.bouncer.challenge.model.Challenge;
import nl.hetckm.bouncer.verdict.model.Verdict;
import nl.hetckm.bouncer.verification.model.Verification;

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
