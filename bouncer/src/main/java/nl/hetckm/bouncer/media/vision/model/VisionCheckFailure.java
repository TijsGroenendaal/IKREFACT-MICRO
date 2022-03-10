package nl.hetckm.bouncer.media.vision.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.cloud.vision.v1.Feature;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.media.model.Media;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter @Setter
public class VisionCheckFailure {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    String reason;

    @Enumerated(EnumType.STRING)
    Feature.Type type;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "challenge-vision")
    private Media media;

    public VisionCheckFailure(String reason, Feature.Type type, Media media) {
        this.reason = reason;
        this.type = type;
        this.media = media;
    }

    public VisionCheckFailure() {}
}
