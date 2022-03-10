package nl.hetckm.bouncer.verdict;

import nl.hetckm.base.enums.ChallengeStatus;
import nl.hetckm.base.enums.VerificationStatus;
import nl.hetckm.base.enums.WebhookChange;
import nl.hetckm.base.enums.WebhookType;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.exceptions.VerdictAlreadyExists;
import nl.hetckm.base.model.Challenge;
import nl.hetckm.base.model.Verdict;
import nl.hetckm.base.model.VerdictAddModel;
import nl.hetckm.base.model.VerdictResponse;
import nl.hetckm.bouncer.challenge.ChallengeService;
import nl.hetckm.bouncer.helper.RelationHelper;
import nl.hetckm.bouncer.verification.VerificationService;
import nl.hetckm.bouncer.webhooks.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerdictService {

    private final VerdictRepository verdictRepository;
    private final ChallengeService challengeService;
    private final WebhookService webhookService;
    private final VerificationService verificationService;

    @Autowired
    public VerdictService(
            VerdictRepository verdictRepository,
            WebhookService webhookService,
            ChallengeService challengeService,
            VerificationService verificationService
    ) {
        this.verdictRepository = verdictRepository;
        this.webhookService = webhookService;
        this.challengeService = challengeService;
        this.verificationService = verificationService;
    }

    public Verdict create(UUID challengeId, UUID verificationId, VerdictAddModel verdictAddModel) {
        Challenge challenge = challengeService.findOne(challengeId, verificationId);

        UUID platformId = RelationHelper.getPlatformId();

        RelationHelper.isFromParent(challenge.getVerification().getId(), verificationId, Verdict.class);
        RelationHelper.isFromParent(challenge.getVerification().getPlatform().getId(), platformId, Verdict.class);

        if (challenge.getVerdict() != null) throw new VerdictAlreadyExists();

        Verdict verdict = new Verdict();
        verdict.setReason(verdictAddModel.getReason());
        verdict.setApproved(verdictAddModel.isAccepted());
        verdict.setChallenge(challenge);

        verdictRepository.save(verdict);

        challengeService.patch(challengeId, verificationId, verdictAddModel.isAccepted() ? ChallengeStatus.ACCEPTED : ChallengeStatus.REJECTED);

        updateVerification(verdict);

        webhookService.trigger(platformId, WebhookType.VERDICT, WebhookChange.CREATE, new VerdictResponse(verdict));
        return verdict;
    }

    public Verdict findByChallenge(UUID challengeId, UUID verificationId) {
        Challenge challenge = challengeService.findOne(challengeId, verificationId);

        RelationHelper.isFromParent(challenge.getVerification().getId(), verificationId, Verdict.class);
        RelationHelper.isFromParent(challenge.getVerification().getPlatform().getId(), RelationHelper.getPlatformId(), Verdict.class);

        return verdictRepository.findByChallenge(challenge).orElseThrow(() -> new EntityNotFoundException(Verdict.class));
    }

    private void updateVerification(Verdict verdict) {
        if (verdict.getApproved()) {
            // Update only the reviewed marker if all challenges are reviewed.
            this.verificationService.update(verdict.getChallenge().getVerification().getId(), VerificationStatus.ACCEPTED);
        }
    }

}
