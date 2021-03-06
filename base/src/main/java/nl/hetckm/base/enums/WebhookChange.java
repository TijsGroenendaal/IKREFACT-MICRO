package nl.hetckm.base.enums;

/**
 * The enum Webhook change, specifies which C(R)UD operation the webhook fires for.
 */
public enum WebhookChange {
    /**
     * Create webhook change.
     */
    CREATE,
    /**
     * Update webhook change.
     */
    UPDATE,
    /**
     * Delete webhook change.
     */
    DELETE,
}
