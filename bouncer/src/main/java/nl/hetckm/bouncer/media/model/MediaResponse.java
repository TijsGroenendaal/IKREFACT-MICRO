package nl.hetckm.bouncer.media.model;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.media.exif.model.ExifResponse;
import nl.hetckm.bouncer.media.vision.model.Face;
import nl.hetckm.bouncer.media.vision.model.Landmark;
import nl.hetckm.bouncer.media.vision.model.VisionCheckFailure;
import nl.hetckm.bouncer.media.vision.model.WebPageMatch;

import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class MediaResponse {

    private UUID id;

    private long size;
    private String originalName;
    private String filePath;
    private String contentType;
    private String extractedText;
    private String labels;
    private int faceCount;
    private ExifResponse exif;
    private Set<Face> faces;
    private Set<Landmark> landmarks;
    private Set<WebPageMatch> webPageMatches;
    private Set<VisionCheckFailure> visionCheckFailures;

    public MediaResponse(Media media) {
        this.id = media.getId();
        this.size = media.getSize();
        this.originalName = media.getOriginalName();
        this.filePath = media.getFilePath();
        this.contentType = media.getContentType();
        this.exif = new ExifResponse(media.getExif());
        if (media.getFaces() == null) {
            this.faceCount = 0;
        } else {
            this.faceCount = media.getFaces().size();
        }
        this.extractedText = media.getExtractedText();
        this.labels = media.getLabels();
        this.faces = media.getFaces();
        this.landmarks = media.getLandmarks();
        this.webPageMatches = media.getWebPageMatches();
        this.visionCheckFailures = media.getVisionCheckFailures();
    }

}
