package nl.hetckm.bouncer.preset;

import nl.hetckm.bouncer.preset.model.Preset;
import nl.hetckm.bouncer.preset.model.PresetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/preset")
@RestController
public class PresetController {

    private final PresetService presetService;

    @Autowired
    public PresetController(PresetService presetService) {
        this.presetService = presetService;
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
    @GetMapping()
    public Page<PresetResponse> getPresets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Preset> presets = presetService.getAllPresetsFromPlatform(pageable);
        List<PresetResponse> presetResponseList = new ArrayList<>();
        presets.forEach(preset -> presetResponseList.add(new PresetResponse(preset)));
        return new PageImpl<>(presetResponseList, pageable, presets.getTotalElements());
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
    @GetMapping(value = "/{presetId}")
    public PresetResponse getPreset(@PathVariable UUID presetId) {
        return new PresetResponse(presetService.getPreset(presetId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public PresetResponse addPreset(@RequestBody Preset presetEntity) {
        return new PresetResponse(presetService.addPreset(presetEntity));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{presetId}")
    public void deletePreset(@PathVariable UUID presetId) {
        presetService.deletePreset(presetId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{presetId}")
    public PresetResponse patchPreset(@PathVariable UUID presetId, @RequestBody Preset preset) {
        return new PresetResponse(presetService.patchPreset(presetId, preset));
    }
}
