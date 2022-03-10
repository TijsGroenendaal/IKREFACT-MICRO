package nl.hetckm.bouncer.media;

import nl.hetckm.bouncer.media.model.Media;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepository extends PagingAndSortingRepository<Media, UUID> {

}
