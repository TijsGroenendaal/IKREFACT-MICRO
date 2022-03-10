package nl.hetckm.bouncer.media;

import nl.hetckm.base.exceptions.NoFileAssociatedWithMediaException;
import nl.hetckm.base.exceptions.UnsupportedMediaFormatException;
import nl.hetckm.base.model.Exif;
import nl.hetckm.base.model.Media;
import nl.hetckm.base.model.MediaResponse;
import nl.hetckm.bouncer.media.exif.ExifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequestMapping("/verification/{verificationId}/challenge/{challengeId}/media")
@RestController
public class MediaController {
    private final MediaService mediaService;
    private final ExifService exifService;

    @Autowired
    public MediaController(
            MediaService mediaService,
            ExifService exifService
    ) {
        this.mediaService = mediaService;
        this.exifService = exifService;
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping()
    public Iterable<MediaResponse> getAllMedia(@PathVariable UUID challengeId, @PathVariable UUID verificationId) {
        return mediaService.findAll(challengeId, verificationId);
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping("/{id}")
    public MediaResponse getMedia(@PathVariable UUID id, @PathVariable UUID challengeId, @PathVariable UUID verificationId) {
        return new MediaResponse(mediaService.findOne(id, challengeId, verificationId));
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @GetMapping("/{id}/file")
    public HttpEntity<byte[]> getMediaFile(@PathVariable UUID id, @PathVariable UUID challengeId, @PathVariable UUID verificationId) {
        Media media = mediaService.findOne(id, challengeId, verificationId);
        try {
            return mediaService.getMediaFile(media);
        } catch (IOException e) {
            mediaService.delete(id, challengeId, verificationId);
            throw new NoFileAssociatedWithMediaException("This media entity has no file uploaded to it");
        }
    }

    @PreAuthorize("hasAuthority('PLATFORM')")
    @PostMapping()
    public MediaResponse addMedia(@RequestParam MultipartFile file, @PathVariable UUID challengeId, @PathVariable UUID verificationId) {
        try {
            Exif exif = exifService.getExif(file.getBytes());
            return mediaService.create(file, challengeId, verificationId, exif);
        } catch (IOException e) {
            throw new UnsupportedMediaFormatException(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN', 'PLATFORM')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExif(@PathVariable UUID id, @PathVariable UUID challengeId, @PathVariable UUID verificationId) {
        mediaService.delete(id, challengeId, verificationId);
    }

}
