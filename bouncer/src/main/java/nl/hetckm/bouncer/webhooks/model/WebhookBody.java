package nl.hetckm.bouncer.webhooks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Webhook body.
 */
@Getter @Setter
@AllArgsConstructor
public class WebhookBody {

    /**
     * The secret to verify the webhook came from this API
     */
    public String secret;

    /**
     * The Entity type.
     */
    public WebhookType entityType;
    /**
     * The Type.
     */
    public WebhookChange type;
    /**
     * The Entity.
     */
    public Object entity;

}
