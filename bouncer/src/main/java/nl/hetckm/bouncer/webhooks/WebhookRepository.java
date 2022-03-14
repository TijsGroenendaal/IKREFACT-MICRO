package nl.hetckm.bouncer.webhooks;

import nl.hetckm.base.enums.WebhookType;
import nl.hetckm.base.model.bouncer.Platform;
import nl.hetckm.base.model.bouncer.Webhook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The interface Webhook repository.
 */
@Repository
public interface WebhookRepository extends PagingAndSortingRepository<Webhook, UUID> {
    /**
     * Find by platform iterable.
     *
     * @param platform the platform
     * @return the iterable
     */
    Page<Webhook> findByPlatform(Platform platform, Pageable pageable);

    /**
     * Find by platform and type iterable.
     *
     * @param platform the platform
     * @param type     the type
     * @return the iterable
     */
    Iterable<Webhook> findByPlatformAndType(Platform platform, WebhookType type);
}
