package nl.hetckm.bouncer.predefs;

import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.predefs.model.Predef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PredefRepository extends PagingAndSortingRepository<Predef, UUID> {
    Page<Predef> findAllByPlatform(Platform platform, Pageable pageable);
}
