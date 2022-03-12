package nl.hetckm.bouncer.preset;

import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.Platform;
import nl.hetckm.base.model.Preset;
import nl.hetckm.bouncer.platform.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PresetService {

    private final PresetRepository presetRepository;
    private final PlatformService platformService;


    @Autowired
    public PresetService(PresetRepository presetRepository, PlatformService platformService) {
        this.presetRepository = presetRepository;
        this.platformService = platformService;
    }

    public Page<Preset> getAllPresetsFromPlatform(Pageable pageable) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        return presetRepository.getAllByPlatform(platform, pageable);
    }

    public Preset getPreset(UUID ID) {
        final Preset preset = presetRepository.findById(ID).orElseThrow(() -> new EntityNotFoundException(Preset.class));
        RelationHelper.isFromParent(preset.getPlatform().getId(), RelationHelper.getPlatformId(), Preset.class);
        return preset;
    }

    public Preset addPreset(Preset preset) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        preset.setPlatform(platform);
        return presetRepository.save(preset);
    }

    public void deletePreset(UUID ID) {
        try {
            presetRepository.deleteById(ID);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(Preset.class);
        }
    }

    public Preset patchPreset(UUID ID, Preset preset) {
        final Preset oldPreset = presetRepository.findById(ID).orElseThrow(() -> new EntityNotFoundException(Preset.class));
        oldPreset.setChanges(preset);

        return presetRepository.save(oldPreset);
    }
}
