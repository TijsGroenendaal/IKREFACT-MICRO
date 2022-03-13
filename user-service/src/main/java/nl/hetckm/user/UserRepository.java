package nl.hetckm.user;

import nl.hetckm.base.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<AppUser, UUID>{
    Optional<AppUser> findByUsername(String username);
}
