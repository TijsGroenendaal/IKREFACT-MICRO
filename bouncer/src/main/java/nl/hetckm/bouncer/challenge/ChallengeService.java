package nl.hetckm.bouncer.challenge;

import nl.hetckm.bouncer.challenge.model.Challenge;
import nl.hetckm.bouncer.challenge.model.ChallengeResponse;
import nl.hetckm.bouncer.challenge.model.ChallengeStatus;
import nl.hetckm.bouncer.exceptions.EntityClosedException;
import nl.hetckm.bouncer.exceptions.EntityNotFoundException;
import nl.hetckm.bouncer.exceptions.NotAllChallengesReviewedException;
import nl.hetckm.bouncer.exceptions.VerificationLifetimeReachedException;
import nl.hetckm.bouncer.helper.RelationHelper;
import nl.hetckm.bouncer.platform.PlatformService;
import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.preset.PresetService;
import nl.hetckm.bouncer.preset.model.Preset;
import nl.hetckm.bouncer.verification.VerificationService;
import nl.hetckm.bouncer.verification.model.Verification;
import nl.hetckm.bouncer.verification.model.VerificationStatus;
import nl.hetckm.bouncer.webhooks.WebhookService;
import nl.hetckm.bouncer.webhooks.model.WebhookChange;
import nl.hetckm.bouncer.webhooks.model.WebhookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private VerificationService verificationService;
    private final PlatformService platformService;
    private final PresetService presetService;

    private final WebhookService webhookService;

    private String environment = "develop";

    @Autowired
    public ChallengeService(
            ChallengeRepository challengeRepository,
            WebhookService webhookService,
            PlatformService platformService,
            PresetService presetService
    ) {
        this.challengeRepository = challengeRepository;
        this.webhookService = webhookService;
        this.platformService = platformService;
        this.presetService = presetService;
    }

    @Autowired
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public Page<Challenge> findAll(UUID verificationId, Pageable pageable) {
        Verification verification = verificationService.findOne(verificationId);
        return challengeRepository.findAllByVerification(verification, pageable);
    }

    public Challenge findOne(UUID challengeId, UUID verificationId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new EntityNotFoundException(Challenge.class));
        RelationHelper.isFromParent(challenge.getVerification().getId(), verificationId, Challenge.class);
        RelationHelper.isFromParent(challenge.getVerification().getPlatform().getId(), RelationHelper.getPlatformId(), Challenge.class);
        return challenge;
    }

    public Challenge save(Preset preset, UUID verificationId) {
        final Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        final int maxDaysLifetime = platform.getSetting().getMaxDaysLifetime();
        Verification verification = verificationService.findOne(verificationId);

        verifyVerificationStatus(verification);
        if (environment.equalsIgnoreCase("production")) {
            verifyAllChallengesReviewed(verification);
        }
        verifyVerificationMaxChallenges(verification);

        Challenge challenge = new Challenge(preset);
        challenge.setChallengeStatus(ChallengeStatus.OPEN);
        challenge.setName(preset.getChallengeText());
        challenge.setVerification(verification);
        challenge.setExpiryDate(Date.from(LocalDateTime.now().plusDays(maxDaysLifetime).toInstant(ZoneOffset.UTC)));
        challengeRepository.save(challenge);
        ChallengeResponse challengeResponse = new ChallengeResponse(challenge);
        webhookService.trigger(
                verification.getPlatform().getId(),
                WebhookType.CHALLENGE,
                WebhookChange.CREATE,
                challengeResponse
        );
        return challenge;
    }

    public Challenge save(UUID presetId, UUID verificationId) {
        final Preset preset = presetService.getPreset(presetId);
        return save(preset, verificationId);
    }

    /**
     * @deprecated Challenges should not be able to be deleted
     */
    public void delete(UUID id, UUID verificationId) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Challenge.class));
        RelationHelper.isFromParent(challenge.getVerification().getId(), verificationId, Challenge.class);
        RelationHelper.isFromParent(challenge.getVerification().getPlatform().getId(), RelationHelper.getPlatformId(), Challenge.class);
        webhookService.trigger(
                challenge.getVerification().getId(),
                WebhookType.CHALLENGE,
                WebhookChange.DELETE,
                new ChallengeResponse(challenge)
        );
        challengeRepository.delete(challenge);
    }

    public Challenge patch(UUID challengeId, UUID verificationId, ChallengeStatus status) {
        final Verification verification = verificationService.findOne(verificationId);
        verifyVerificationStatus(verification);
        final Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException(Challenge.class));
        verifyChallengeReviewed(challenge);

        RelationHelper.isFromParent(challenge.getVerification().getId(), verificationId, Challenge.class);
        RelationHelper.isFromParent(challenge.getVerification().getPlatform().getId(), RelationHelper.getPlatformId(), Challenge.class);
        challenge.setChallengeStatus(status);
        challengeRepository.save(challenge);

        webhookService.trigger(
                challenge.getVerification().getPlatform().getId(),
                WebhookType.CHALLENGE,
                WebhookChange.UPDATE,
                new ChallengeResponse(challenge)
        );
        return challenge;
    }

    /**
     * Verify that the Verification has not been reviewed.
     * if the verification has been reviewed throw Exception
     */
    private void verifyVerificationStatus(Verification verification) {
        if (!verification.getStatus().equals(VerificationStatus.OPEN) ) throw new EntityClosedException(Verification.class, verification);
    }

    /**
     * Verify that the MaxChallengesLifeTime has not been reached.
     * If the MaxChallengesLifeTime has been reached throw an Exception
     */
    private void verifyVerificationMaxChallenges(Verification verification) {
        if (challengeRepository.countChallengeByVerification(verification) >= verification.getMaxChallengeLifetime()) {
            throw new VerificationLifetimeReachedException(verification);
        }
    }

    /**
     * Verify that a Challenge has not yet been reviewed
     * If the Challenge has been reviewed throw an Exception
     */
    private void verifyChallengeReviewed(Challenge challenge) {
        if (!challenge.getChallengeStatus().equals(ChallengeStatus.OPEN)) {
            throw new EntityClosedException(Challenge.class, challenge);
        }
    }

    /**
     * Verify that all Challenges have been reviewed.
     * If not all Challenges have been reviewed throw an Exception
     */
    private void verifyAllChallengesReviewed(Verification verification) {
        challengeRepository.findAllByVerification(verification).forEach(challenge -> {
            if (challenge.getChallengeStatus().equals(ChallengeStatus.OPEN)) throw new NotAllChallengesReviewedException();
        });
    }
}
