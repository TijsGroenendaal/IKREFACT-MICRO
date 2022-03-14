package nl.hetckm.bouncer.media;

import nl.hetckm.base.exceptions.UnsupportedMediaFormatException;
import nl.hetckm.base.model.bouncer.UploadResult;
import org.apache.tika.Tika;

import java.io.IOException;

public interface StorageSolution {
    UploadResult upload(byte[] data, String filename) throws IOException;
    byte[] download(String path) throws IOException;
    void remove(String path);

    default String getContentType(byte[] bytes) {
        Tika tika = new Tika();
        String detectedType = tika.detect(bytes);
        if (!detectedType.contains("image") && detectedType.contains("video")) {
            throw new UnsupportedMediaFormatException("The format: " + detectedType + " is not supported.");
        }
        return detectedType;
    }
}
