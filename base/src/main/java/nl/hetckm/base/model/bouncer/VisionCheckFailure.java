package nl.hetckm.base.model.bouncer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.enums.BouncerFeature;
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
    BouncerFeature type;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "challenge-vision")
    private Media media;

    public VisionCheckFailure(String reason, Enum<?> type, Media media) {
        this.reason = reason;
        this.type = BouncerFeature.valueOf(type.toString());
        this.media = media;
    }

    public VisionCheckFailure() {}
}
