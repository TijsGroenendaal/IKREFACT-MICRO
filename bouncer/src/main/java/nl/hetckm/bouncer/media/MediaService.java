package nl.hetckm.bouncer.media;

import nl.hetckm.base.enums.StorageType;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.model.*;
import nl.hetckm.bouncer.challenge.ChallengeService;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.bouncer.media.aws.S3Service;
import nl.hetckm.bouncer.media.filesystem.FileSystemService;
import nl.hetckm.bouncer.media.vision.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final ChallengeService challengeService;
    private final S3Service s3Service;
    private final FileSystemService fileSystemService;
    private final VisionService visionService;
    private final EncryptionService encryptionService;

    private final String environment = "develop";

    @Autowired
    public MediaService(
            MediaRepository mediaRepository,
            ChallengeService challengeService,
            S3Service s3Service,
            FileSystemService fileSystemService,
            VisionService visionService,
            EncryptionService encryptionService
    ) {
        this.mediaRepository = mediaRepository;
        this.challengeService = challengeService;
        this.s3Service = s3Service;
        this.fileSystemService = fileSystemService;
        this.visionService = visionService;
        this.encryptionService = encryptionService;
    }

    public Media findOne(UUID mediaId, UUID challengeId, UUID verificationId) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new EntityNotFoundException(Media.class));

        RelationHelper.isFromParent(media.getChallenge().getId(), challengeId, Media.class);
        RelationHelper.isFromParent(media.getChallenge().getVerification().getId(), verificationId, Media.class);
        RelationHelper.isFromParent(media.getChallenge().getVerification().getPlatform().getId(), RelationHelper.getPlatformId(), Media.class);

        return media;
    }

    public Iterable<MediaResponse> findAll(UUID challengeId, UUID verificationId) {
        Set<MediaResponse> toReturn = new HashSet<>();

        checkRelations(challengeId, verificationId);

        mediaRepository.findAll().forEach(m -> {
            if (m.getChallenge().getId().equals(challengeId)) {
                toReturn.add(new MediaResponse(m));
            }
        });
        return toReturn;
    }

    public MediaResponse create(MultipartFile file, UUID challengeId, UUID verificationId, Exif exif) throws IOException {
        Challenge challenge = checkRelations(challengeId, verificationId);

        byte[] bytes = file.getBytes();

        String encryptionKey = challenge.getVerification().getPlatform().getEncryptionKey();
        byte[] encryptedData;
        try {
            encryptedData = encryptionService.encrypt(encryptionKey, bytes);
        } catch (GeneralSecurityException e) {
            throw new ServerErrorException("Unable to encrypt media. " + e.getMessage(), e);
        }

        Media media = new Media();

        UploadResult uploadResult;
        if (environment.equalsIgnoreCase("production")) {
            uploadResult = s3Service.upload(encryptedData, file.getOriginalFilename());
            media.setStorageType(StorageType.S3);
        } else {
            uploadResult = fileSystemService.upload(encryptedData, file.getOriginalFilename());
            media.setStorageType(StorageType.FILESYSTEM);
        }

        media.setChallenge(challenge);
        media.setExif(exif);
        media.setFilePath(uploadResult.getPath());
        media.setContentType(s3Service.getContentType(bytes));
        media.setSize(file.getSize());
        media.setOriginalName(file.getOriginalFilename());

        Media savedMedia = mediaRepository.save(media);

        visionService.analyzeImage(bytes, savedMedia, challenge);

        return new MediaResponse(media);
    }

    public void delete(UUID mediaId, UUID challengeId, UUID verificationId) {
        Media media = findOne(mediaId, challengeId, verificationId);
        mediaRepository.delete(media);
        String filePath = media.getFilePath();
        if (media.getStorageType() == StorageType.S3) {
            s3Service.remove(filePath);
        } else {
            fileSystemService.remove(filePath);
        }
    }

    public void save(Media media) {
        mediaRepository.save(media);
    }

    public HttpEntity<byte[]> getMediaFile(Media media) throws IOException {
        String filePath = media.getFilePath();
        byte[] content;
        if (media.getStorageType() == StorageType.S3) {
           content = s3Service.download(filePath);
        } else {
            content = fileSystemService.download(filePath);
        }

        String encryptionKey = media.getChallenge().getVerification().getPlatform().getEncryptionKey();
        byte[] decryptedContent;
        try {
            decryptedContent = encryptionService.decrypt(encryptionKey, content);
        } catch (GeneralSecurityException e) {
            throw new ServerErrorException("Unable to decrypt media. " + e.getMessage(), e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(media.getContentType()));
        return new HttpEntity<>(decryptedContent, headers);
    }

    private Challenge checkRelations(UUID challengeId, UUID verificationId) {
        Challenge challenge = challengeService.findOne(challengeId, verificationId);
        RelationHelper.isFromParent(challenge.getVerification().getId(), verificationId, Media.class);
        RelationHelper.isFromParent(challenge.getVerification().getPlatform().getId(), RelationHelper.getPlatformId(), Media.class);
        return challenge;
    }

}
