package nl.hetckm.presetservice.preset;

import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.preset.Preset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PresetService {

    private final PresetRepository presetRepository;

    @Autowired
    public PresetService(PresetRepository presetRepository) {
        this.presetRepository = presetRepository;
    }

    public Page<Preset> getAllPresetsFromPlatform(Pageable pageable) {
        return presetRepository.getAllByPlatformId(RelationHelper.getPlatformId(), pageable);
    }

    public Preset getPreset(UUID ID) {
        final Preset preset = presetRepository.findById(ID).orElseThrow(() -> new EntityNotFoundException(Preset.class));
        RelationHelper.isFromParent(preset.getPlatformId(), RelationHelper.getPlatformId(), Preset.class);
        return preset;
    }

    public Preset addPreset(Preset preset) {
        preset.setPlatformId(RelationHelper.getPlatformId());
        return presetRepository.save(preset);
    }

    public void deletePreset(UUID ID) {
        final Preset preset = presetRepository.findById(ID).orElseThrow(() -> new EntityNotFoundException(Preset.class));
        RelationHelper.isFromParent(preset.getPlatformId(), RelationHelper.getPlatformId(), Preset.class);

        try {
            presetRepository.deleteById(ID);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(Preset.class);
        }
    }

    public Preset patchPreset(UUID ID, Preset preset) {
        final Preset oldPreset = presetRepository.findById(ID).orElseThrow(() -> new EntityNotFoundException(Preset.class));
        RelationHelper.isFromParent(oldPreset.getPlatformId(), RelationHelper.getPlatformId(), Preset.class);
        oldPreset.setChanges(preset);

        return presetRepository.save(oldPreset);
    }

    public void deleteByPlatform(UUID platformId) {
        presetRepository.deleteAllByPlatformId(platformId);
    }
}
