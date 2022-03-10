package nl.hetckm.bouncer.verdict;

import nl.hetckm.base.model.Challenge;
import nl.hetckm.base.model.Verdict;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerdictRepository extends CrudRepository<Verdict, UUID> {
    Optional<Verdict> findByChallenge(Challenge challenge);
}
