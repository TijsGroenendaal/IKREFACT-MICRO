package nl.hetckm.bouncer.media.exif;

import nl.hetckm.bouncer.media.exif.model.Exif;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExifRepository extends PagingAndSortingRepository<Exif, UUID> {


}
