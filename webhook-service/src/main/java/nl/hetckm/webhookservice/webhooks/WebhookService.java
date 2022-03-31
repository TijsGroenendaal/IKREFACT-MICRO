package nl.hetckm.webhookservice.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.webhook.WebhookTriggerRequest;
import nl.hetckm.base.model.webhook.Webhook;
import nl.hetckm.base.model.webhook.WebhookBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * The Webhook service.
 */
@Service
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    /**
     * Instantiates a new Webhook service.
     *
     * @param webhookRepository  the webhook repository
     */
    @Autowired
    public WebhookService(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    /**
     * Trigger a webhook for a platform with a specific type and change.
     *
     * @param platformId the platform id
     */
    public void trigger(UUID platformId, WebhookTriggerRequest request) {
        RelationHelper.isFromParent(platformId, RelationHelper.getPlatformId(), Webhook.class);

        Iterable<Webhook> webhooks = webhookRepository.findByPlatformIdAndType(platformId, request.getType());
        WebClient client = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "bouncer-api")
                .build();
        webhooks.forEach(webhook -> {
            UriSpec<RequestBodySpec> uriSpec = client.post();
            RequestBodySpec bodySpec = uriSpec.uri(webhook.getUrl());

            WebhookBody webhookBody = new WebhookBody(webhook.getSecret(), request.getType(), request.getWebhookChange(), request.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            String data;
            try {
                data = mapper.writeValueAsString(webhookBody);
            } catch (JsonProcessingException e) {
                logger.error("Unable to parse data for " + webhook.getType() + " webhook.", e);
                return;
            }

            RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(data);
            headersSpec.retrieve()
                    .onStatus(HttpStatus::isError, response -> Mono.empty())
                    .toBodilessEntity().subscribe(response -> {
                        webhook.setLastStatusCode(response.getStatusCodeValue());
                        webhook.setLastError(response.getStatusCode().isError());
                        this.webhookRepository.save(webhook);
                    });
        });
    }


    /**
     * Send a test request to the webhook
     * This uses the Testable interface and WebhookType enum to get the test objects.
     *
     * @param webhookId the webhook id
     */
    public void test(UUID webhookId) {
        Webhook webhook = findOne(webhookId);
        Object testObject = webhook.getType().getTestable().getTestObject();
        trigger(
                RelationHelper.getPlatformId(),
                new WebhookTriggerRequest(
                        webhook.getType(),
                        WebhookChange.CREATE,
                        testObject
                )
        );
    }

    /**
     * Creates a webhook response.
     *
     * @param requestWebhook the request webhook
     * @return the webhook response
     */
    public Webhook create(Webhook requestWebhook) {
        Webhook webhook = new Webhook();
        webhook.setPlatformId(RelationHelper.getPlatformId());
        webhook.setType(requestWebhook.getType());
        webhook.setUrl(requestWebhook.getUrl());
        webhook.setSecret(requestWebhook.getSecret());

        webhookRepository.save(webhook);
        return webhook;
    }

    /**
     * Finds all webhooks.
     *
     * @return Iterable with WebhookResponses
     */
    public Page<Webhook> findAll(Pageable pageable) {
        UUID platformId = RelationHelper.getPlatformId();
        return webhookRepository.findByPlatformId(RelationHelper.getPlatformId(), pageable);
    }

    /**
     * Find one webhook.
     *
     * @param id the id
     * @return the webhook response
     */
    public Webhook findOne(UUID id) {
        Webhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Webhook.class));
        RelationHelper.isFromParent(RelationHelper.getPlatformId(), RelationHelper.getPlatformId(), Webhook.class);
        return webhook;
    }

    /**
     * Delete.
     *
     * @param id the webhook id
     */
    public void delete(UUID id) {
        try {
            Webhook webhook = webhookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Webhook.class));
            RelationHelper.isFromParent(webhook.getPlatformId(), RelationHelper.getPlatformId(), Webhook.class);
            webhookRepository.delete(webhook);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(Webhook.class);
        }
    }


}
