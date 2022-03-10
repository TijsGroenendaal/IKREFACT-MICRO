package nl.hetckm.bouncer.media.exif.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ExifResponse {

    private final UUID id;

    private final String
            gpsLat,
            gpsLatRef,
            gpsLong,
            gpsLongRef,
            phoneModel,
            originalDateString,
            gpsDateString,
            lastModifiedDateString,
            flash;
    private final LocalDateTime
            originalDate,
            lastModifiedDate,
            gpsDate;

    public ExifResponse(Exif exif) {
        this.id = exif.getId();
        this.gpsLat = exif.getGpsLat();
        this.gpsLatRef = exif.getGpsLatRef();
        this.gpsLong = exif.getGpsLong();
        this.gpsLongRef = exif.getGpsLongRef();
        this.phoneModel = exif.getPhoneModel();
        this.originalDateString = exif.getOriginalDateString();
        this.gpsDateString = exif.getGpsDateString();
        this.lastModifiedDateString = exif.getLastModifiedDateString();
        this.flash = exif.getFlash();
        this.originalDate = exif.getOriginalDate();
        this.lastModifiedDate = exif.getLastModifiedDate();
        this.gpsDate = exif.getGpsDate();
    }
}
