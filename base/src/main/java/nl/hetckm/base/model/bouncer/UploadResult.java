package nl.hetckm.base.model.bouncer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UploadResult {
    String path;
    String contentType;
    long size;
}
