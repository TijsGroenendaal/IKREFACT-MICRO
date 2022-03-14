package nl.hetckm.bouncer.predefs;

import nl.hetckm.base.model.bouncer.Predef;
import nl.hetckm.base.model.bouncer.PredefResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/predef")
public class PredefController {

    private final PredefService predefService;

    @Autowired
    public PredefController(
            PredefService predefService
    ) {
        this.predefService = predefService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @GetMapping()
    public Page<PredefResponse> getPredefs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Predef> predefs = predefService.getAllPredefFromPlatform(pageable);
        List<PredefResponse> predefResponseList = new ArrayList<>();
        predefs.forEach(predef -> predefResponseList.add(new PredefResponse(predef)));
        return new PageImpl<>(predefResponseList, pageable, predefs.getTotalElements());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @GetMapping("/{id}")
    public PredefResponse getpredef(
            @PathVariable UUID id
    ) {
        return new PredefResponse(predefService.getPredef(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public PredefResponse addPredef(
            @RequestBody Predef predef
    ) {
        return new PredefResponse(predefService.addPredef(predef));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deletePredef(
            @PathVariable UUID id
    ) {
        predefService.deletePreset(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public PredefResponse patchPredef(
            @PathVariable UUID id,
            @RequestBody Predef predef
    ) {
        return new PredefResponse(predefService.patchPredef(id, predef));
    }
}

