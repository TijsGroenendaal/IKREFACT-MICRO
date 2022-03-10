package nl.hetckm.bouncer.challenge;

import nl.hetckm.bouncer.challenge.model.Challenge;
import nl.hetckm.bouncer.challenge.model.ChallengeResponse;
import nl.hetckm.bouncer.challenge.model.ChallengeUpdate;
import nl.hetckm.bouncer.challenge.model.NewChallengeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/verification/{verificationId}/challenge")
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping()
    public Page<ChallengeResponse> getChallengesByVerification(
            @PathVariable UUID verificationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Challenge> challenges = challengeService.findAll(verificationId, pageable);
        List<ChallengeResponse> challengeResponseList = new ArrayList<>();
        challenges.forEach(challenge -> challengeResponseList.add(new ChallengeResponse(challenge)));
        return new PageImpl<>(challengeResponseList, pageable, challenges.getTotalElements());
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping("/{id}")
    public ChallengeResponse getChallenge(
            @PathVariable UUID verificationId,
            @PathVariable UUID id) {
        return new ChallengeResponse(challengeService.findOne(id, verificationId));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR')")
    @PostMapping()
    public @ResponseBody ChallengeResponse addChallenge(
            @RequestBody NewChallengeRequest newChallengeRequest,
            @PathVariable UUID verificationId) {
        return new ChallengeResponse(challengeService.save(newChallengeRequest.getPresetId(), verificationId));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR')")
    @PatchMapping("/{id}")
    public ChallengeResponse patchChallenge(
            @PathVariable UUID verificationId,
            @PathVariable UUID id,
            @RequestBody ChallengeUpdate challengeUpdate
            ) {
        return new ChallengeResponse(challengeService.patch(
                id,
                verificationId,
                challengeUpdate.getStatus()));
    }

}
