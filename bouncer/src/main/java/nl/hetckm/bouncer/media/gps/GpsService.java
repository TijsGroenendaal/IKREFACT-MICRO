package nl.hetckm.bouncer.media.gps;

import nl.hetckm.base.model.bouncer.Exif;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GpsService {

    private final static double earthRadius = 6371; //Kilometers

    public double getEarthRadius() {
        return earthRadius;
    }

    /**
     * @param reference  represents the hemisphere of the coordinate.
     * @param gpsLongLat represents the GPS longitude or latitude in Hours, Minutes and Seconds
     * @return returns a map coordinate from a different format
     */
    public double normalizeCoordinates(String reference, String gpsLongLat) {
        ArrayList<Double> usableValues = new ArrayList<>();
        String[] split =
                gpsLongLat
                        .replace("°", "")
                        .replace("'", "")
                        .replace("\"", "")
                        .replace(",", ".")
                        .split(" ");

        for (String s : split) {
            usableValues.add(Double.parseDouble(s));
        }

        double result = usableValues.get(0) + usableValues.get(1) / 60 + usableValues.get(2) / 3600;
        if (reference.equals("E") || reference.equals("S")) {
            return result * -1;
        } else {
            return result;
        }
    }

    /**
     * @param longitudeLandmark longitude of position1, landmark
     * @param latitudeLandmark  latitude of position1, landmark
     * @param longitudePos2     longitude of position2, EXIF
     * @param latitudePos2      longitude of position2, EXIF
     * @return returns the Haversine
     */
    public double calculateDistanceHaversine(
            double longitudeLandmark,
            double latitudeLandmark,
            double longitudePos2,
            double latitudePos2,
            double radius
    ) {
        return 2 * radius
                * Math.asin(Math.pow(Math.sin(Math.toRadians(latitudePos2 - latitudeLandmark) / 2), 2)
                + Math.pow(Math.sin(Math.toRadians(longitudePos2 - longitudeLandmark) / 2), 2)
                * Math.cos(Math.toRadians(latitudeLandmark))
                * Math.cos(Math.toRadians(latitudePos2)));
    }

    /**
     * @param range the maximum distance these two points may be separated
     * @return returns whether 2 points were approximately close enough
     */
    public boolean isCloseEnough(double distance, double range) {
        return distance <= range;
    }

    /**
     * @param imageID           Used to find the image that needs to be checked
     * @param challengeID       used to find the challenge associated with the image
     * @param verificationID    used to find the verification associated with the challenge and thus image
     * @param longitudeLandmark refers to the longitudinal position of the landmark that should be in the image
     * @param latitudeLandmark  refers to the latitudinal position of the landmark that should be in the image.
     * @param maxRange          refers to the max distance that can be between the location of the landmark and the position in the image
     * @return returns true if the distance between exifLocation and landmarkLocation is equal to or smaller than maxRange
     */
    public boolean performLocationCheck(
            double longitudeLandmark,
            double latitudeLandmark,
            double maxRange,
            Exif exif
    ) {
        return isCloseEnough(calculateDistanceHaversine(
                        longitudeLandmark,
                        latitudeLandmark,
                        normalizeCoordinates(exif.getGpsLongRef(), exif.getGpsLong()),
                        normalizeCoordinates(exif.getGpsLatRef(), exif.getGpsLat()),
                        earthRadius),
                maxRange);
    }

    public boolean performLocationCheckNoNormalize(
            double longitudeLandmark,
            double latitudeLandmark,
            double longitudeVerify,
            double latitudeVerify,
            double maxRange,
            double earthRadius
    ) {
        return isCloseEnough(calculateDistanceHaversine(
                        longitudeLandmark,
                        latitudeLandmark,
                        longitudeVerify,
                        latitudeVerify,
                        earthRadius),
                maxRange);
    }
}
