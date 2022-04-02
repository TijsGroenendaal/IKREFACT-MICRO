package nl.hetckm.presetservice.preset;

import nl.hetckm.base.model.preset.Preset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface PresetRepository extends PagingAndSortingRepository<Preset, UUID> {
    Page<Preset> getAllByPlatformId(UUID platformId, Pageable pageable);
    @Transactional
    void deleteAllByPlatformId(UUID platformId);
}
