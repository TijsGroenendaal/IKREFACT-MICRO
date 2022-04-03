package nl.hetckm.webhookservice.webhooks;

import nl.hetckm.base.enums.WebhookType;
import nl.hetckm.base.model.webhook.Webhook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The interface Webhook repository.
 */
@Repository
public interface WebhookRepository extends PagingAndSortingRepository<Webhook, UUID> {
    /**
     * Find by platform iterable.
     *
     * @param platformId the platform
     * @return the iterable
     */
    Page<Webhook> findByPlatformId(UUID platformId, Pageable pageable);

    /**
     * Find by platform and type iterable.
     *
     * @param platform the platform
     * @param type     the type
     * @return the iterable
     */
    Iterable<Webhook> findByPlatformIdAndType(UUID platform, WebhookType type);
    @Transactional
    void deleteAllByPlatformId(UUID platformId);
}
