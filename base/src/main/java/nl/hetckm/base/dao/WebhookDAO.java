package nl.hetckm.base.dao;

import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.enums.WebhookType;
import nl.hetckm.base.helper.HttpHelper;
import nl.hetckm.base.model.webhook.WebhookTriggerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class WebhookDAO {

    @Value("${WEBHOOK_SERVICE_PORT}")
    private String webhookServicePort;

    private final HttpHelper httpHelper;

    private final WebClient webClient;

    @Autowired
    public WebhookDAO(HttpHelper httpHelper) {
        this.webClient = WebClient.create();
        this.httpHelper = httpHelper;
    }

    public void trigger(UUID webhookId, WebhookType type, WebhookChange webhookChange, Object entity) {
        webClient
                .post()
                .uri("http://webhook-service:"+ webhookServicePort +"/webhook/trigger/" + webhookId)
                .body(Mono.just(new WebhookTriggerRequest(type, webhookChange, entity)), WebhookTriggerRequest.class)
                .header("Authorization", httpHelper.createHttpAuthorizationHeaderValues())
                .retrieve().toBodilessEntity().doOnError(mono -> {
                    // TODO save failed transaction
                }).subscribe();
    }

    public void deleteAllByPlatform(UUID platformId) {
        webClient
                .delete()
                .uri("http://webhook-service:" + webhookServicePort + "/webhook/platform=" + platformId)
                .header("Authorization", httpHelper.createHttpAuthorizationHeaderValues())
                .retrieve().toBodilessEntity().doOnError(mono -> {
                    // TODO save failed transaction
                }).subscribe();
    }
}
