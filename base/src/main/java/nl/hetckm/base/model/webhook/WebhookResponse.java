package nl.hetckm.base.model.webhook;

import lombok.Getter;
import nl.hetckm.base.enums.WebhookType;

import java.util.UUID;

/**
 * The type Webhook response.
 */
@Getter
public class WebhookResponse {

    private UUID id;
    private String url;
    private String secret;
    private WebhookType type;
    private UUID platform;
    private int lastStatusCode;
    private boolean lastError;

    /**
     * Instantiates a new Webhook response.
     *
     * @param webhook the webhook
     */
    public WebhookResponse(Webhook webhook) {
        this.id = webhook.getId();
        this.url = webhook.getUrl();
        this.secret = webhook.getSecret();
        this.type = webhook.getType();
        this.platform = webhook.getPlatformId();
        this.lastStatusCode = webhook.getLastStatusCode();
        this.lastError = webhook.isLastError();
    }

}
