package nl.hetckm.bouncer.preset;

import nl.hetckm.base.model.Platform;
import nl.hetckm.base.model.Preset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PresetRepository extends PagingAndSortingRepository<Preset, UUID> {
    Page<Preset> getAllByPlatform(Platform platform, Pageable pageable);
}
