package nl.hetckm.bouncer.verification;

import nl.hetckm.bouncer.challenge.ChallengeService;
import nl.hetckm.bouncer.challenge.model.Challenge;
import nl.hetckm.bouncer.exceptions.EntityNotFoundException;
import nl.hetckm.bouncer.helper.RelationHelper;
import nl.hetckm.bouncer.media.model.Media;
import nl.hetckm.bouncer.platform.PlatformService;
import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.preset.model.Preset;
import nl.hetckm.bouncer.verification.model.Verification;
import nl.hetckm.bouncer.verification.model.VerificationResponse;
import nl.hetckm.bouncer.verification.model.VerificationStatus;
import nl.hetckm.bouncer.webhooks.WebhookService;
import nl.hetckm.bouncer.webhooks.model.WebhookChange;
import nl.hetckm.bouncer.webhooks.model.WebhookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final PlatformService platformService;
    private ChallengeService challengeService;
    private final WebhookService webhookService;

    @Autowired
    public VerificationService(
            VerificationRepository verificationRepository,
            PlatformService platformService,
            WebhookService webhookService
    ) {
        this.verificationRepository = verificationRepository;
        this.platformService = platformService;
        this.webhookService = webhookService;
    }

    @Autowired
    public void setChallengeService(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    public Verification create(Verification request) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());

        Verification verification = new Verification();
        verification.setStatus(VerificationStatus.OPEN);
        verification.setName(request.getName());
        verification.setPlatform(platform);
        verification.setMaxChallengeLifetime(platform.getSetting().getMaxChallengesLifetime());
        verificationRepository.save(verification);
        final Preset preset = new Preset();
        preset.setUseCoordinateMatching(false);
        preset.setUseFaceDetection(false);
        preset.setUseLandmarkDetection(false);
        preset.setUseWebDetection(false);
        preset.setUseTextDetection(false);
        preset.setChallengeText("Submit the image of your advertisement");
        challengeService.save(preset, verification.getId());
        webhookService.trigger(platform.getId(), WebhookType.VERIFICATION, WebhookChange.CREATE, new VerificationResponse(verification));
        return verification;
    }

    public Page<Verification> findAll(Pageable pageable) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        return verificationRepository.findAllByPlatform(platform, pageable);
    }

    public Verification findOne(UUID id) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Verification.class));
        RelationHelper.isFromParent(verification.getPlatform().getId(), RelationHelper.getPlatformId(), Verification.class);
        return verification;
    }

    public Verification update(UUID id, VerificationStatus status) {
        Verification existingVerification = verificationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Verification.class));

        RelationHelper.isFromParent(existingVerification.getPlatform().getId(), RelationHelper.getPlatformId(), Verification.class);

        existingVerification.setStatus(status);

        verificationRepository.save(existingVerification);

        webhookService.trigger(
                existingVerification.getPlatform().getId(),
                WebhookType.VERIFICATION,
                WebhookChange.UPDATE,
                new VerificationResponse(existingVerification)
        );
        return existingVerification;
    }

    /**
     * @deprecated Verification should not be able to be deleted
     */
    public void delete(UUID id) {
        try {
            Verification verification = verificationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Verification.class));
            RelationHelper.isFromParent(verification.getPlatform().getId(), RelationHelper.getPlatformId(), Verification.class);
            webhookService.trigger(
                    verification.getPlatform().getId(),
                    WebhookType.VERIFICATION,
                    WebhookChange.DELETE,
                    new VerificationResponse(verification)
            );
            verificationRepository.delete(verification);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(Verification.class);
        }
    }

    public List<String> getAllMediaFilePaths(UUID verificationId) {
        Page<Challenge> challengePage = challengeService.findAll(verificationId, Pageable.unpaged());
        List<String> filePaths = new ArrayList<>();
        for (Challenge challenge : challengePage) {
            final Optional<Media> media = challenge.getMedia().stream().findFirst();
            if (media.isPresent()) {
                String filePath =
                        "/verification/" + verificationId +
                        "/challenge/" + challenge.getId() +
                        "/media/" + media.get().getId() + "/file";
                filePaths.add(filePath);
            }
        }
        return filePaths;
    }
}
