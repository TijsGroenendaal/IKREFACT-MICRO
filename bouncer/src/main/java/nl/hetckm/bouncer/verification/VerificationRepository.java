package nl.hetckm.bouncer.verification;

import nl.hetckm.base.model.bouncer.Platform;
import nl.hetckm.base.model.bouncer.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VerificationRepository extends PagingAndSortingRepository<Verification, UUID> {
    Page<Verification> findAllByPlatform(Platform platform, Pageable pageable);
}
