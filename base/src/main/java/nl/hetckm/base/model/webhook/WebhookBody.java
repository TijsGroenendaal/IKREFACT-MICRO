package nl.hetckm.base.model.webhook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.enums.WebhookType;

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
