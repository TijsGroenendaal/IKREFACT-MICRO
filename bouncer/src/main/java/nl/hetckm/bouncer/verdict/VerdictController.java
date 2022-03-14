package nl.hetckm.bouncer.verdict;

import nl.hetckm.base.model.bouncer.VerdictAddModel;
import nl.hetckm.base.model.bouncer.VerdictResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path= "/verification/{verificationId}/challenge/{challengeId}/verdict")
public class VerdictController {

    private final VerdictService verdictService;

    @Autowired
    public VerdictController(VerdictService verdictService) {
        this.verdictService = verdictService;
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    VerdictResponse createVerdict(
            @PathVariable UUID verificationId,
            @PathVariable UUID challengeId,
            @RequestBody VerdictAddModel verdictAddModel) {
        return new VerdictResponse(verdictService.create(challengeId, verificationId, verdictAddModel));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping()
    public @ResponseBody VerdictResponse getChallengeVerdict(
            @PathVariable UUID verificationId,
            @PathVariable UUID challengeId) {
        return new VerdictResponse(verdictService.findByChallenge(challengeId, verificationId));
    }
}
