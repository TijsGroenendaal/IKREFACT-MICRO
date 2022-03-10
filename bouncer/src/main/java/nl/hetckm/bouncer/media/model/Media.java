package nl.hetckm.bouncer.media.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.challenge.model.Challenge;
import nl.hetckm.bouncer.media.StorageType;
import nl.hetckm.bouncer.media.exif.model.Exif;
import nl.hetckm.bouncer.media.vision.model.Face;
import nl.hetckm.bouncer.media.vision.model.Landmark;
import nl.hetckm.bouncer.media.vision.model.VisionCheckFailure;
import nl.hetckm.bouncer.media.vision.model.WebPageMatch;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Entity
public class Media {

     @Id
     @GeneratedValue(generator = "uuid")
     @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
     @org.hibernate.annotations.Type(type="uuid-char")
     private UUID id;

     private long size;
     private String originalName;
     private String filePath;
     private String contentType;

     private String extractedText;
     private String labels;

     private boolean isCloseEnoughExifChallenge;
     private boolean isCloseEnoughVisionChallenge;
     private boolean isCloseEnoughExifVision;

     @Enumerated(EnumType.STRING)
     private StorageType storageType;

     @OneToOne(cascade = CascadeType.ALL)
     private Exif exif;

     @ManyToOne(cascade = CascadeType.DETACH)
     private Challenge challenge;

     @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonManagedReference(value = "media-landmark")
     private Set<Landmark> landmarks;

     @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonManagedReference(value = "media-face")
     private Set<Face> faces;

     @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonManagedReference(value = "media-webpage")
     private Set<WebPageMatch> webPageMatches;

     @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonManagedReference(value = "challenge-vision")
     private Set<VisionCheckFailure> visionCheckFailures;

}
