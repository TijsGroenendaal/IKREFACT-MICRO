package nl.hetckm.bouncer.verification;

import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.verification.model.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VerificationRepository extends PagingAndSortingRepository<Verification, UUID> {
    Page<Verification> findAllByPlatform(Platform platform, Pageable pageable);
}
