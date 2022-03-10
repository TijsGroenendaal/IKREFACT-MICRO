package nl.hetckm.bouncer.media.filesystem;

import nl.hetckm.base.model.UploadResult;
import nl.hetckm.bouncer.media.StorageSolution;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemService implements StorageSolution {

    @Value("${filesystem.data-directory}")
    private String storagePath;

    @Override
    public UploadResult upload(byte[] data, String filename) throws IOException {
        String contentType = getContentType(data);

        String extension = FilenameUtils.getExtension(filename);
        String filePath = storagePath + "/" + UUID.randomUUID() + "." + extension;

        Path path = Paths.get(filePath);
        Files.write(path, data);

        return new UploadResult(filePath, contentType, data.length);
    }

    @Override
    public byte[] download(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    @Override
    public void remove(String path) {
        File file = new File(path);
        file.delete();
    }
}
