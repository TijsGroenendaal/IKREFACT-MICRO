package nl.hetckm.bouncer.verification;

import nl.hetckm.bouncer.exceptions.ForbiddenException;
import nl.hetckm.bouncer.verification.model.Verification;
import nl.hetckm.bouncer.verification.model.VerificationResponse;
import nl.hetckm.bouncer.verification.model.VerificationReviewed;
import nl.hetckm.bouncer.verification.model.VerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PreAuthorize("hasAnyAuthority('PLATFORM')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    VerificationResponse createVerification (@RequestBody Verification request) {
        return new VerificationResponse(verificationService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping()
    public @ResponseBody
    Page<VerificationResponse> getAllVerifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Verification> verifications = verificationService.findAll(pageable);
        List<VerificationResponse> verificationResponseList = new ArrayList<>();
        verifications.forEach(verification -> verificationResponseList.add(new VerificationResponse(verification)));
        return new PageImpl<>(verificationResponseList, pageable, verifications.getTotalElements());
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping("/{id}")
    public @ResponseBody
    VerificationResponse getVerification(@PathVariable UUID id) {
        return new VerificationResponse(verificationService.findOne(id));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
    @PatchMapping("/{id}")
    public @ResponseBody
    VerificationResponse updateVerification(
            @PathVariable UUID id,
            @RequestBody VerificationReviewed verificationReviewed
            ) {
        if (verificationReviewed.getStatus().equals(VerificationStatus.ACCEPTED)) throw new ForbiddenException("Cannot manually set verification to accepted");
        return new VerificationResponse(verificationService.update(id, verificationReviewed.getStatus()));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping("/{id}/media")
    public List<String> getAllMediaFilePaths(@PathVariable UUID id) {
        return verificationService.getAllMediaFilePaths(id);
    }

}
