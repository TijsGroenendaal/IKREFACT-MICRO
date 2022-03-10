package nl.hetckm.bouncer.user;

import nl.hetckm.base.model.AppUser;
import nl.hetckm.base.model.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<AppUser, UUID> {
    boolean existsByUsername(String username);
    Optional<AppUser> findByUsername(String username);
    Page<AppUser> findByPlatform(Platform platform, Pageable pageable);
}
