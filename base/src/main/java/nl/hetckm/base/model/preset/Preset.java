package nl.hetckm.base.model.preset;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter @Setter
public class Preset {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;
    private String challengeText;

    private boolean useLandmarkDetection;
    private String landmarkMatch = "";
    private boolean useTextDetection;
    private String textMatch = "";
    private boolean useFaceDetection;
    private boolean useWebDetection;
    private boolean useCoordinateMatching;
    private double maxRange;
    private double longitude;
    private double latitude;
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID platformId;

    public void setChanges(Preset preset) {
        this.challengeText = preset.challengeText;
        this.useLandmarkDetection = preset.useLandmarkDetection;
        this.landmarkMatch = preset.landmarkMatch;
        this.useTextDetection = preset.useTextDetection;
        this.textMatch = preset.textMatch;
        this.useFaceDetection = preset.useFaceDetection;
        this.useWebDetection = preset.useWebDetection;
        this.useCoordinateMatching = preset.useCoordinateMatching;
        this.maxRange = preset.maxRange;
        this.longitude = preset.longitude;
        this.latitude = preset.latitude;
    }

}
