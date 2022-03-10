package nl.hetckm.bouncer.challenge;

import nl.hetckm.bouncer.challenge.model.Challenge;
import nl.hetckm.bouncer.challenge.model.ChallengeStatus;
import nl.hetckm.bouncer.verification.model.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends PagingAndSortingRepository<Challenge, UUID> {
    Iterable<Challenge> findAllByVerification(Verification verification);
    int countChallengeByVerification(Verification verification);
    Iterable<Challenge> findAllByExpiryDateBeforeAndChallengeStatus(Date date, ChallengeStatus challengeStatus);
    Page<Challenge> findAllByVerification(Verification verification, Pageable pageable);
}
