package nl.hetckm.base.model.webhook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.enums.WebhookType;

@Getter @Setter
@AllArgsConstructor
public class WebhookTriggerRequest{
    WebhookType type;
    WebhookChange webhookChange;
    Object entity;
}
