package nl.hetckm.base.model;

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
    private PlatformResponse platform;
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
        this.platform = new PlatformResponse(webhook.getPlatform());
        this.lastStatusCode = webhook.getLastStatusCode();
        this.lastError = webhook.isLastError();
    }

}
