package nl.hetckm.bouncer.media.vision;

import com.google.cloud.vision.v1.*;
import nl.hetckm.base.interfaces.VertexContainer;
import nl.hetckm.base.model.*;
import nl.hetckm.base.model.Vertex;
import nl.hetckm.bouncer.media.MediaRepository;
import nl.hetckm.bouncer.media.gps.GpsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VisionService {

    private final Logger logger = LoggerFactory.getLogger(VisionService.class);
    private final CloudVisionTemplate cloudVisionTemplate;
    private final MediaRepository mediaRepository;
    private final GpsService gpsService;


    public VisionService(
            @Autowired(required = false) CloudVisionTemplate cloudVisionTemplate,
            MediaRepository mediaRepository, GpsService gpsService
    ) {
        this.cloudVisionTemplate = cloudVisionTemplate;
        this.mediaRepository = mediaRepository;
        this.gpsService = gpsService;
    }

    public void analyzeImage(byte[] image, Media media, Challenge challenge) {
        if (cloudVisionTemplate == null) return;
        Feature.Type[] featureTypes = getAnalyzeFeatures(challenge);
        if (featureTypes.length == 0) return;
        ByteArrayResource imageResource = new ByteArrayResource(image);
        AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(imageResource, featureTypes);
        if (challenge.isUseTextDetection()) extractText(imageResource, media);
        if (challenge.isUseWebDetection()) extractWeb(response.getWebDetection(), media);
        if (challenge.isUseFaceDetection()) extractFaces(response.getFaceAnnotationsList(), media);
        if (challenge.isUseLandmarkDetection() || challenge.isUseCoordinateMatching()) extractLandmarks(response.getLandmarkAnnotationsList(), media);
        checkFailures(media, challenge);
        mediaRepository.save(media);
    }

    private void checkFailures(Media media, Challenge challenge) {
        Set<VisionCheckFailure> failures = new HashSet<>();
        if (challenge.isUseTextDetection() && !challenge.getTextMatch().equals("")) {
            if (!matchText(media.getExtractedText(), challenge.getTextMatch())) {
                String reason = "The submitted image text does not match: '" + challenge.getTextMatch() + "'.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.TEXT_DETECTION, media));
            }
        }
        if (challenge.isUseLandmarkDetection() && !challenge.getLandmarkMatch().equals("")) {
            boolean match = false;
            for (Landmark landmark : media.getLandmarks()) {
                if (matchText(landmark.getDescription(), challenge.getLandmarkMatch())) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                String reason = "The landmark with matcher: '" + challenge.getLandmarkMatch() + "' was not found in the image.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.LANDMARK_DETECTION, media));
            }
        }
        if (challenge.isUseFaceDetection()) {
            if (media.getFaces() == null || media.getFaces().isEmpty()) {
                String reason = "No faces were detected in this image.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.FACE_DETECTION, media));
            }
        }
        if (challenge.isUseWebDetection()) {
            int fullMatches = media.getWebPageMatches().stream().mapToInt(WebPageMatch::getFullMatches).sum();
            if (fullMatches > 0) {
                String reason = "This image was found on the internet " + fullMatches + " times.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.WEB_DETECTION, media));
            }
        }

        Exif exif = media.getExif();
        if (challenge.isUseCoordinateMatching()) {

            // No exif
            boolean hasExif = true;
            if (!mediaHasExifLocation(exif)) {
                hasExif = false;
                String reason = "No EXIF location data could be found.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.UNRECOGNIZED, media));
            }
            // No vision
            boolean hasVision = true;
            if (!mediaHasVisionLocation(media)) {
                hasVision = false;
                String reason = "No location could be extracted from the image with AI.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.UNRECOGNIZED, media));
            }

            // Skip when no exif or vision
            // Vision vs exif in range
            if (hasExif && hasVision && !isCloseEnoughExifVision(media, challenge.getMaxRange())) {
                String reason = "Exif GPS location is too far away from AI extracted GPS location or could not be computed.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.UNRECOGNIZED, media));
            }

            // Skip when no vision
            // Vision in range
            if (hasVision && !isCloseEnoughVisionChallenge(challenge.getLongitude(), challenge.getLatitude(),
                    media, challenge.getMaxRange())) {
                String reason = "AI Extracted GPS location exceeded maximum range.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.UNRECOGNIZED, media));
            }

            // Skip when no exif
            // Range vs exif
            if (hasExif && !isCloseEnoughExifChallenge(challenge.getLongitude(), challenge.getLatitude(), challenge.getMaxRange(), media.getExif())) {
                String reason = "Exif GPS range exceeded maximum range or could not be computed.";
                failures.add(new VisionCheckFailure(reason, Feature.Type.UNRECOGNIZED, media));
            }
        }

        media.setVisionCheckFailures(failures);
    }

    private boolean mediaHasExifLocation(Exif exif) {
        return !(exif.getGpsLat().equals("Unknown") || exif.getGpsLong().equals("Unknown"));
    }

    private boolean mediaHasVisionLocation(Media media) {
        return !media.getLandmarks().isEmpty();
    }

    private boolean matchText(String text, String matcher) {
        if (text.toLowerCase().contains(matcher.toLowerCase())) {
            return true;
        } else return text.toLowerCase().matches(matcher);
    }

    private void extractFaces(List<FaceAnnotation> faceAnnotations, Media media) {
        Set<Face> faces = new HashSet<>();
        for (FaceAnnotation faceAnnotation : faceAnnotations) {
            Face face = new Face();
            face.setVertexes(boundingPolyToVertexes(faceAnnotation.getBoundingPoly(), face));
            face.setMedia(media);
            faces.add(face);
        }
        media.setFaces(faces);
    }

    private void extractLandmarks(List<EntityAnnotation> landmarkAnnotations, Media media) {
        Set<Landmark> landmarks = new HashSet<>();
        for (EntityAnnotation landmarkNotation : landmarkAnnotations) {
            LocationInfo info = landmarkNotation.getLocationsList().listIterator().next();
            String latitude = info.hasLatLng() ? String.valueOf(info.getLatLng().getLatitude()) : "Unknown";
            String longitude = info.hasLatLng() ? String.valueOf(info.getLatLng().getLongitude()) : "Unknown";
            Landmark landmark = new Landmark();
            landmark.setDescription(landmarkNotation.getDescription());
            landmark.setLatitude(latitude);
            landmark.setLongitude(longitude);
            landmark.setVertexes(boundingPolyToVertexes(landmarkNotation.getBoundingPoly(), landmark));
            landmark.setMedia(media);
            landmarks.add(landmark);
        }
        media.setLandmarks(landmarks);
    }

    private void extractWeb(WebDetection webDetection, Media media) {
        List<String> labels = webDetection.getBestGuessLabelsList()
                .stream()
                .map(WebDetection.WebLabel::getLabel)
                .collect(Collectors.toList());
        String joinedLabels = String.join(", ", labels);
        media.setLabels(joinedLabels);

        Set<WebPageMatch> matches = new HashSet<>();
        for (WebDetection.WebPage page : webDetection.getPagesWithMatchingImagesList()) {
            WebPageMatch match = new WebPageMatch();
            match.setTitle(page.getPageTitle());
            match.setUrl(page.getUrl());
            match.setFullMatches(page.getFullMatchingImagesCount());
            match.setPartialMatches(page.getPartialMatchingImagesCount());
            match.setMedia(media);
            matches.add(match);
        }
        media.setWebPageMatches(matches);
    }

    private void extractText(Resource image, Media media) {
        String extractedText = cloudVisionTemplate.extractTextFromImage(image);
        media.setExtractedText(extractedText);
    }

    private Feature.Type[] getAnalyzeFeatures(Challenge challenge) {
        List<Feature.Type> features = new ArrayList<>();
        if (challenge.isUseFaceDetection()) features.add(Feature.Type.FACE_DETECTION);
        if (challenge.isUseLandmarkDetection() || challenge.isUseCoordinateMatching()) features.add(Feature.Type.LANDMARK_DETECTION);
        if (challenge.isUseWebDetection()) features.add(Feature.Type.WEB_DETECTION);
        Feature.Type[] featuresArray = new Feature.Type[features.size()];
        return features.toArray(featuresArray);
    }

    private Set<Vertex> boundingPolyToVertexes(BoundingPoly boundingPoly, VertexContainer parent) {
        return boundingPoly.getVerticesList().stream().map(vertex -> {
            Vertex persistentVertex = new Vertex();
            persistentVertex.setX(vertex.getX());
            persistentVertex.setY(vertex.getY());
            if (parent instanceof Face face) {
                persistentVertex.setFace(face);
            } else if (parent instanceof Landmark landmark) {
                persistentVertex.setLandmark(landmark);
            }
            return persistentVertex;
        }).collect(Collectors.toSet());
    }

    /**
     * @param verificationID the Verification ID with which the image in question is associated.
     * @param media          used to find a specific Landmark contained with an image.
     * @return true/false depending on whether the locations are within 5 kilometers of each other
     * This function tests whether the location data present in a digital medium is close enough to the Vision AI landmark.
     * This is a second safety measure and functions as a preventive tool against green screen trickery.
     */
    public boolean isCloseEnoughExifVision(
            Media media,
            double maxRange
    ) {
        try {
            Landmark landmark = media.getLandmarks().stream().findFirst().orElse(null);
            if (landmark == null) return false;
            return gpsService.performLocationCheck(
                    Double.parseDouble(landmark.getLongitude()),
                    Double.parseDouble(landmark.getLatitude()),
                    maxRange, media.getExif());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Exception encountered in Exif/Vision");
            return false;
        }
    }

    /**
     * @param longitudeChallenge the latitude associated with a challenge
     * @param latitudeChallenge  the latitude associated with a challenge
     * @param media              used to locate the specific Landmark within an image.
     * @param imageID            the ID of the Image in question
     * @return true/false depending on whether the locations are within 5 kilometers of each other
     * This function calculates whether the Vision AI landmark is close enough to the actual landmark.
     * This is done to ensure accuracy and to detect any potential problems.
     */
    public boolean isCloseEnoughVisionChallenge(
            double longitudeChallenge,
            double latitudeChallenge,
            Media media,
            double maxRange
    ) {
        try {
            Landmark landmark = media.getLandmarks().stream().findFirst().orElse(null);
            if (landmark == null) return false;
            return gpsService.performLocationCheckNoNormalize(
                    Double.parseDouble(landmark.getLongitude()),
                    Double.parseDouble(landmark.getLatitude()),
                    longitudeChallenge, latitudeChallenge, maxRange, gpsService.getEarthRadius());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Exception encountered in Vision/Challenge");
            return false;
        }
    }


    /**
     * @param longitudeChallenge the latitude associated with a challenge
     * @param latitudeChallenge  the latitude associated with a challenge
     * @return true/false depending on whether the locations are within 5 kilometers of each other
     * This function calculates whether the image in question was taken close enough the expected location.
     */
    public boolean isCloseEnoughExifChallenge(
            double longitudeChallenge,
            double latitudeChallenge,
            double maxRange,
            Exif exif
    ) {
        try {
            return gpsService.performLocationCheck(longitudeChallenge,
                    latitudeChallenge, maxRange, exif);
        } catch (Exception e) {
            logger.error("Exception encountered in Exif/Challenge");
            return false;
        }
    }
}
