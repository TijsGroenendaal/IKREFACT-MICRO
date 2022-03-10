package nl.hetckm.bouncer.preset.model;

import lombok.Data;

import java.util.UUID;

@Data
public class PresetResponse {
    private UUID platformId;
    private String challengeText;
    private UUID Id;

    private boolean useLandmarkDetection;
    private String landmarkMatch;
    private boolean useTextDetection;
    private String textMatch;
    private boolean useFaceDetection;
    private boolean useWebDetection;
    private boolean useCoordinateMatching;
    private double maxRange;
    private double longitude;
    private double latitude;


    public PresetResponse(Preset entity) {
        this.Id = entity.getId();
        this.challengeText = entity.getChallengeText();
        this.platformId = entity.getPlatform().getId();
        this.useLandmarkDetection = entity.isUseLandmarkDetection();
        this.landmarkMatch = entity.getLandmarkMatch();
        this.useTextDetection = entity.isUseTextDetection();
        this.textMatch = entity.getTextMatch();
        this.useFaceDetection = entity.isUseFaceDetection();
        this.useWebDetection = entity.isUseWebDetection();
        this.useCoordinateMatching = entity.isUseCoordinateMatching();
        this.maxRange = entity.getMaxRange();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
    }
}
