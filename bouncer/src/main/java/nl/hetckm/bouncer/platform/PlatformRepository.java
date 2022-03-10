package nl.hetckm.bouncer.platform;

import nl.hetckm.base.model.Platform;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformRepository extends PagingAndSortingRepository<Platform, UUID> {
    Optional<Platform> findByApiKey(String apikey);
}
