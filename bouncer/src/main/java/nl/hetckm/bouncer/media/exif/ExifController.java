package nl.hetckm.bouncer.media.exif;

import nl.hetckm.bouncer.media.MediaService;
import nl.hetckm.bouncer.media.exif.model.ExifResponse;
import nl.hetckm.bouncer.media.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/verification/{verificationId}/challenge/{challengeId}/media/{imageId}/exif")
@RestController
public class ExifController {
    private final MediaService mediaService;

    @Autowired
    public ExifController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
    @GetMapping()
    public ExifResponse getExif(@PathVariable UUID imageId, @PathVariable UUID challengeId, @PathVariable UUID verificationId) {
        Media media = mediaService.findOne(imageId,challengeId, verificationId);
        return new ExifResponse(media.getExif());
    }

}
