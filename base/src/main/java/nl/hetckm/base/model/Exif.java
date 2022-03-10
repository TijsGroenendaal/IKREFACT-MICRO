package nl.hetckm.base.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.UUID;

@Entity
@Getter @Setter
public class Exif {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String
            gpsLat,
            gpsLatRef,
            gpsLong,
            gpsLongRef,
            phoneModel,
            originalDateString,
            gpsDateString,
            lastModifiedDateString,
            flash;
    private LocalDateTime
            originalDate,
            lastModifiedDate,
            gpsDate;

    @OneToOne(mappedBy = "exif", cascade = CascadeType.ALL, orphanRemoval = true)
    private Media media;

    public Exif(LinkedHashMap<String, String> exifData) {
        this.originalDateString = exifData.get("Date/Time Original");
        this.gpsDateString = exifData.get("GPS Time-Stamp");
        this.lastModifiedDateString = exifData.get("File Modified Date");

        this.originalDate = parseDate(this.originalDateString, "yyyy:MM:dd HH:mm:ss");
        this.gpsDate = parseDate(this.gpsDateString, "yyyy:MM:dd");
        this.lastModifiedDate = parseDate(this.lastModifiedDateString, "EEE MMM dd HH:mm:ss xxx yyyy");

        this.phoneModel = exifData.get("Model");
        this.flash = exifData.get("Flash");

        this.gpsLat = exifData.get("GPS Latitude");
        this.gpsLatRef = exifData.get("GPS Latitude Ref");
        this.gpsLong = exifData.get("GPS Longitude");
        this.gpsLongRef = exifData.get("GPS Longitude Ref");
    }

    private LocalDateTime parseDate(String timestamp, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(timestamp, formatter);
        } catch (IllegalArgumentException | DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    public Exif() {}
}
