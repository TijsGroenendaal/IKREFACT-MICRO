package nl.hetckm.base.dao;

import nl.hetckm.base.enums.Role;
import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.enums.WebhookType;
import nl.hetckm.base.helper.JwtHelper;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.webhook.WebhookTriggerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WebhookDAO {

    @Value("${AUTHORITIES_CLAIM_NAME}")
    private String AUTHORITIES_CLAIM_NAME;

    @Value("${WEBHOOK_SERVICE_PORT}")
    private String webhookServicePort;

    private final JwtHelper jwtHelper;

    private final RestTemplate restTemplate;

    @Autowired
    public WebhookDAO(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new HttpClientErrorHandler());
    }

    public void trigger(UUID webhookId, WebhookType type, WebhookChange webhookChange, Object entity) {
        final Map<String, Object> authorities = new HashMap<>();
        authorities.put(AUTHORITIES_CLAIM_NAME, Role.SERVICE + " " + RelationHelper.getPlatformId());

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtHelper.createJwtForClaims(
                "service",
                authorities
        ));

        final HttpEntity<WebhookTriggerRequest> httpEntity = new HttpEntity<>(new WebhookTriggerRequest(type, webhookChange, entity), headers);
        restTemplate.exchange(
                "http://webhook-service:"+ webhookServicePort +"/webhook/trigger/" + webhookId,
                HttpMethod.POST,
                httpEntity,
                Void.class
        ).getBody();
    }
}
