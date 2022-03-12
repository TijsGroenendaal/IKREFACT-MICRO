package nl.hetckm.bouncer.predefs;

import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.Platform;
import nl.hetckm.base.model.Predef;
import nl.hetckm.bouncer.platform.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PredefService {

    private final PredefRepository predefRepository;
    private final PlatformService platformService;

    @Autowired
    public PredefService(
            PredefRepository predefRepository,
            PlatformService platformService
    ) {
        this.predefRepository = predefRepository;
        this.platformService = platformService;
    }

    public Page<Predef> getAllPredefFromPlatform(Pageable pageable) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        return predefRepository.findAllByPlatform(platform, pageable);
    }

    public Predef getPredef(UUID id) {
        return predefRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Predef.class));
    }

    public Predef addPredef(Predef predef) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        predef.setPlatform(platform);
        return predefRepository.save(predef);
    }

    public void deletePreset(UUID id) {
        Predef predef = predefRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Predef.class));
        predefRepository.delete(predef);
    }

    public Predef patchPredef(UUID id, Predef predef) {
        Predef toUpdate = predefRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Predef.class));
        toUpdate.setReason(predef.getReason());
        return predefRepository.save(toUpdate);
    }
}
