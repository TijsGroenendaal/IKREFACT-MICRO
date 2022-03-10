package nl.hetckm.bouncer.challenge.model;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.interfaces.Reviewable;
import nl.hetckm.bouncer.media.model.Media;
import nl.hetckm.bouncer.preset.model.Preset;
import nl.hetckm.bouncer.verdict.model.Verdict;
import nl.hetckm.bouncer.verification.model.Verification;
import nl.hetckm.bouncer.webhooks.model.WebhookTestable;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter
public class Challenge implements WebhookTestable, Reviewable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    @CreatedDate
    private Date createDate = new Date();
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    private String name;
    private String challengeText;
    private boolean useLandmarkDetection;
    private String landmarkMatch;
    private boolean useTextDetection;
    private String textMatch;
    private boolean useFaceDetection;
    private boolean useWebDetection;
    private boolean useCoordinateMatching;
    private double longitude;
    private double latitude;
    private double maxRange;

    @OneToOne(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Verdict verdict;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Verification verification;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Media> media;

    public Challenge(Preset preset) {
        this.challengeText = preset.getChallengeText();
        this.useLandmarkDetection = preset.isUseLandmarkDetection();
        this.landmarkMatch = preset.getLandmarkMatch();
        this.useTextDetection = preset.isUseTextDetection();
        this.textMatch = preset.getTextMatch();
        this.useFaceDetection = preset.isUseFaceDetection();
        this.useWebDetection = preset.isUseWebDetection();
        this.useCoordinateMatching = preset.isUseCoordinateMatching();
        this.longitude = preset.getLongitude();
        this.latitude = preset.getLatitude();
        this.maxRange = preset.getMaxRange();
    }

    public Challenge() {}

    @Override
    public Object getTestObject() {
        Random random = new Random();
        Challenge challenge = new Challenge();
        challenge.setId(UUID.randomUUID());
        challenge.setCreateDate(new Date());
        challenge.setExpiryDate(new Date());
        challenge.setChallengeStatus(ChallengeStatus.values()[random.nextInt(ChallengeStatus.values().length)]);
        challenge.setChallengeText("This is a webhook test challenge.");
        return new ChallengeResponse(challenge);
    }
}
