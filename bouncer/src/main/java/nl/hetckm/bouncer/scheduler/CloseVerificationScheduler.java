package nl.hetckm.bouncer.scheduler;

import nl.hetckm.base.enums.ChallengeStatus;
import nl.hetckm.base.enums.VerificationStatus;
import nl.hetckm.base.model.bouncer.Challenge;
import nl.hetckm.base.model.bouncer.Verification;
import nl.hetckm.bouncer.challenge.ChallengeRepository;
import nl.hetckm.bouncer.verification.VerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class CloseVerificationScheduler {

    private final VerificationRepository verificationRepository;
    private final ChallengeRepository challengeRepository;

    @Autowired
    public CloseVerificationScheduler(
            VerificationRepository verificationRepository,
            ChallengeRepository challengeRepository
    ) {
        this.verificationRepository = verificationRepository;
        this.challengeRepository = challengeRepository;
    }

    /**
     * Schedules this task every hour at minute 1
     */
    @Scheduled(cron = "${scheduler.closeverification.cron.expression}")
    public void closeVerificationScheduler() {
        findOverdueChallenges();
    }

    private void findOverdueChallenges() {
        final Date now = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        final Iterable<Challenge> overdueChallenges = challengeRepository.findAllByExpiryDateBeforeAndChallengeStatus(now, ChallengeStatus.OPEN);
        for (Challenge challenge : overdueChallenges) {
            closeChallenge(challenge);
        }
    }

    private void closeChallenge(Challenge challenge) {
        final Verification verification = verificationRepository.findById(challenge.getVerification().getId()).orElse(null);
        if (verification == null) return;
        challenge.setChallengeStatus(ChallengeStatus.REJECTED);
        if (verification.getMaxChallengeLifetime() <= challengeRepository.countChallengeByVerification(verification)) {
            verification.setStatus(VerificationStatus.REJECTED);
        }
        challengeRepository.save(challenge);
        verificationRepository.save(verification);
    }
}
