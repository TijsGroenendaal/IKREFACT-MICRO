package nl.hetckm.bouncer.challenge;

import nl.hetckm.base.enums.ChallengeStatus;
import nl.hetckm.base.model.bouncer.Challenge;
import nl.hetckm.base.model.bouncer.Verification;
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
