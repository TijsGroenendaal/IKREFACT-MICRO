package nl.hetckm.bouncer.media.exif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.model.bouncer.Exif;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

@Service
public class ExifService {
    @Getter @Setter String[] keywords = {
            "Date/Time Original",
            "Flash",
            "File Modified Date",
            "GPS Latitude",
            "GPS Longitude",
            "GPS Latitude Ref",
            "GPS Longitude Ref",
            "Model",
            "GPS Time-Stamp"
    };

    private final ExifRepository exifRepository;

    @Autowired
    public ExifService(ExifRepository exifRepository) {
        this.exifRepository = exifRepository;
    }

    public Exif getExif(byte[] byteStream) {
        ArrayList<String> exifData = fetchExif(byteStream);
        LinkedHashMap<String, String> filteredData = searchAndCreate(getKeywords(), exifData);
        Exif exif = new Exif(filteredData);
        exifRepository.save(exif);
        return exif;
    }

    private ArrayList<String> fetchExif(byte[] byteStream) {
        ArrayList<String> results = new ArrayList<>();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(byteStream));
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String result = String.format("%s = %s", tag.getTagName(), tag.getDescription());
                    results.add(result);
                }
            }
            return results;
        } catch(Exception e) {
            return results;
        }
    }

    private LinkedHashMap<String, String> searchAndCreate(String[] keyword, ArrayList<String> source){
        LinkedHashMap<String, String> processedResults = new LinkedHashMap<>();
        String[] splitted;
        for (String str : keyword) {
            boolean evidence = false;
            for (String item : source) {
                if(item.contains(str)){
                    splitted = item.split("=");
                    processedResults.put(str, splitted[1]);
                    evidence = true;
                }
            }
            if(!evidence){processedResults.put(str, "Unknown");}
        } return processedResults;
    }

    public LinkedHashMap<String, Boolean> spoofDetection(LinkedHashMap<String, String> exifReport){
        LinkedHashMap<String, Boolean> spoofReport = new LinkedHashMap<>();
        String originalDate = exifReport.get("Date/Time Original");
        spoofReport.put("Enough Exif Data: ", maxAmountOfNull("Unknown", exifReport));
        spoofReport.put("Date Likely Valid: ", matcher(originalDate, exifReport.get("File Modified Date")));
        spoofReport.put("GPS Date Valid: ", inMatcher(originalDate, exifReport.get("GPS Date Stamp")));
        return spoofReport;
    }

    public boolean matcher(Object value1, Object value2){
        return Objects.equals(value1, value2);
    }

    public boolean inMatcher(String value1, String value2){
        return value1.contains(value2);
    }

    public boolean maxAmountOfNull(String searchTerm, LinkedHashMap<String, String> map){
        int i = 0;
        for (String str : map.values()) {
            if (!map.containsValue(searchTerm)){
                i++;
            }
        }return i >= 1;
    }
}
