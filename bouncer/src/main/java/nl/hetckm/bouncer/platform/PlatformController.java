package nl.hetckm.bouncer.platform;

import nl.hetckm.bouncer.platform.model.NewPlatformRequest;
import nl.hetckm.bouncer.platform.model.NewPlatformResponse;
import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.platform.model.PlatformResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/platform")
public class PlatformController {

    private final PlatformService platformService;

    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @PreAuthorize("hasAuthority('SUPERUSER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    NewPlatformResponse createPlatform(@RequestBody NewPlatformRequest platformRequest) {
        return platformService.create(
                platformRequest.getUsername(),
                platformRequest.getPassword(),
                platformRequest.getPlatform()
        );
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/owned")
    public PlatformResponse getOwnedPlatform() {
        return new PlatformResponse(platformService.findOwned());
    }

    @PreAuthorize("hasAuthority('SUPERUSER')")
    @PostMapping("/{id}/reset")
    public @ResponseBody PlatformResponse resetPlatform(@PathVariable UUID id) {
        return platformService.reset(id);
    }

    @PreAuthorize("hasAuthority('SUPERUSER')")
    @GetMapping()
    public @ResponseBody
    Page<PlatformResponse> getPlatforms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Platform> platforms = platformService.findAll(pageable);
        List<PlatformResponse> platformResponseList = new ArrayList<>();
        platforms.forEach(platform -> platformResponseList.add(new PlatformResponse(platform)));
        return new PageImpl<>(platformResponseList, pageable, platforms.getTotalElements());
    }

    @PreAuthorize("hasAuthority('SUPERUSER')")
    @GetMapping("/{id}")
    public @ResponseBody
    PlatformResponse getPlatform(@PathVariable UUID id) {
        return new PlatformResponse(platformService.findOne(id));
    }

    @PreAuthorize("hasAuthority('SUPERUSER')")
    @PatchMapping("/{id}")
    public @ResponseBody
    PlatformResponse updatePlatform(
            @PathVariable UUID id,
            @RequestBody Platform platform
    ) {
        return new PlatformResponse(platformService.update(id, platform));
    }

    @PreAuthorize("hasAuthority('SUPERUSER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlatform(@PathVariable UUID id) {
        platformService.delete(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/owned")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnPlatform() {platformService.deleteOwn();}


}
