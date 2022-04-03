package nl.hetckm.base.dao;

import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.enums.WebhookType;
import nl.hetckm.base.helper.HttpHelper;
import nl.hetckm.base.model.webhook.WebhookTriggerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class WebhookDAO {

    @Value("${WEBHOOK_SERVICE_PORT}")
    private String webhookServicePort;

    private final RestTemplate restTemplate;

    private final HttpHelper httpHelper;

    @Autowired
    public WebhookDAO(HttpHelper httpHelper) {
        this.httpHelper = httpHelper;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new HttpClientErrorHandler());
    }

    public void trigger(UUID webhookId, WebhookType type, WebhookChange webhookChange, Object entity) {
        restTemplate.exchange(
                "http://webhook-service:"+ webhookServicePort +"/webhook/trigger/" + webhookId,
                HttpMethod.POST,
                httpHelper.createHttpAuthorizationHeader(new WebhookTriggerRequest(type, webhookChange, entity)),
                Void.class
        ).getBody();
    }

    public void deleteAllByPlatform(UUID platformId) {
        restTemplate.exchange(
                "http://webhook-service:" + webhookServicePort + "/webhook/platform=" + platformId,
                HttpMethod.DELETE,
                httpHelper.createHttpAuthorizationHeader(""),
                Void.class
        );
    }
}
