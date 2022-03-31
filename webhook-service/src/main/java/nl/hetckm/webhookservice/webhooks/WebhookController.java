package nl.hetckm.webhookservice.webhooks;

import nl.hetckm.base.model.webhook.WebhookTriggerRequest;
import nl.hetckm.base.model.webhook.Webhook;
import nl.hetckm.base.model.webhook.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Webhook controller.
 */
@RestController
@RequestMapping(path = "/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    /**
     * Instantiates a new Webhook controller.
     *
     * @param webhookService the webhook service
     */
    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Creates a new webhook.
     *
     * @param webhook the webhook
     * @return the webhook response
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public @ResponseBody
    WebhookResponse createWebhook(@RequestBody Webhook webhook) {
        return new WebhookResponse(webhookService.create(webhook));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/test")
    public @ResponseBody
    void testWebhook(@PathVariable UUID id) {
        webhookService.test(id);
    }

    /**
     * Gets all webhooks.
     *
     * @return the all webhooks
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public @ResponseBody
    Iterable<WebhookResponse> getAllWebhooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Webhook> webhooks = webhookService.findAll(pageable);
        List<WebhookResponse> webhookResponseList = new ArrayList<>();
        webhooks.forEach(webhook -> webhookResponseList.add(new WebhookResponse(webhook)));
        return new PageImpl<>(webhookResponseList, pageable, webhooks.getTotalElements());
    }

    /**
     * Gets webhook.
     *
     * @param id the id
     * @return the webhook
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public @ResponseBody
    WebhookResponse getWebhook(@PathVariable UUID id) {
        return new WebhookResponse(webhookService.findOne(id));
    }

    /**
     * Delete webhook.
     *
     * @param id the id
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWebhook(@PathVariable UUID id) {
        webhookService.delete(id);
    }

    @PreAuthorize("hasAuthority('SERVICE')")
    @PostMapping("/trigger/{id}")
    public void triggerWebhook(@PathVariable UUID id, @RequestBody WebhookTriggerRequest webhookTriggerRequest) {
        webhookService.trigger(id, webhookTriggerRequest);
    }
}
