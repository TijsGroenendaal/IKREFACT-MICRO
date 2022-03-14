package nl.hetckm.base.model.bouncer;

import lombok.Data;
import nl.hetckm.base.enums.ChallengeStatus;

import java.util.Date;
import java.util.UUID;

@Data
public class ChallengeResponse {

    private UUID Id;
    private Date createDate;
    private Date expiryDate;
    private ChallengeStatus status;
    private String text;
    private boolean useLandmarkDetection;
    private String landmarkMatch;
    private boolean useTextDetection;
    private String textMatch;
    private boolean useFaceDetection;
    private boolean useWebDetection;
    private VerdictResponse verdict;
    private boolean useCoordinateMatching;
    private double longitude;
    private double latitude;
    private double maxRange;

    public ChallengeResponse(Challenge challenge) {
        this.createDate = challenge.getCreateDate();
        this.expiryDate = challenge.getExpiryDate();
        this.Id = challenge.getId();
        this.status = challenge.getChallengeStatus();
        this.text = challenge.getChallengeText();
        this.useLandmarkDetection = challenge.isUseLandmarkDetection();
        this.landmarkMatch = challenge.getLandmarkMatch();
        this.useTextDetection = challenge.isUseTextDetection();
        this.textMatch = challenge.getTextMatch();
        this.useFaceDetection = challenge.isUseFaceDetection();
        this.useWebDetection = challenge.isUseWebDetection();
        this.useCoordinateMatching = challenge.isUseCoordinateMatching();
        this.longitude = challenge.getLongitude();
        this.latitude = challenge.getLatitude();
        this.maxRange = challenge.getMaxRange();
        this.verdict = challenge.getVerdict() == null ? null : new VerdictResponse(challenge.getVerdict());
    }
}
