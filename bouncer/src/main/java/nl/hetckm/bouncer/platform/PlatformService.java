package nl.hetckm.bouncer.platform;

import nl.hetckm.base.dao.PresetDAO;
import nl.hetckm.base.enums.Role;
import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.exceptions.InvalidJwtException;
import nl.hetckm.base.exceptions.UsernameExistsException;
import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.*;
import nl.hetckm.bouncer.media.EncryptionService;
import nl.hetckm.bouncer.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;


@Service
public class PlatformService {

    Logger logger = LoggerFactory.getLogger(PlatformService.class);

    private final PlatformRepository platformRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final PresetDAO presetDAO;

    @Value("${lifetime.challenge}")
    private int MAX_CHALLENGE_LIFETIME;

    @Value("${lifetime.day}")
    private int MAX_DAY_LIFETIME;

    @Autowired
    public PlatformService(
            PlatformRepository platformRepository,
            EncryptionService encryptionService,
            @Lazy UserService userService,
            PresetDAO presetDAO
    ) {
        this.platformRepository = platformRepository;
        this.encryptionService = encryptionService;
        this.userService = userService;
        this.presetDAO = presetDAO;
    }

    public NewPlatformResponse create(String username, String userPassword, Platform platform) {
        if (userService.existsByUsername(username))
            throw new UsernameExistsException("This username is already taken.");
        platform.setApiKey(UUID.randomUUID().toString().replaceAll("-", ""));
        Setting setting = new Setting();
        setting.setMaxDaysLifetime(MAX_DAY_LIFETIME);
        setting.setMaxChallengesLifetime(MAX_CHALLENGE_LIFETIME);
        setting.setPlatform(platform);
        platform.setSetting(setting);
        try {
            platform.setEncryptionKey(Base64.getEncoder().encodeToString(encryptionService.generateKey().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new ServerErrorException("Unable to encrypt media. " + e.getMessage(), e );
        }
        AppUser adminUser = new AppUser();
        adminUser.setRole(Role.ADMIN);
        adminUser.setEnabled(true);
        adminUser.setPlatform(platform);
        adminUser.setUsername(username);
        adminUser.setPassword(userPassword);
        platformRepository.save(platform);
        userService.create(adminUser);
        adminUser.setPlatform(null); // if not set to null it will return platform twice, As root and child of user
        return new NewPlatformResponse(platform, new UserResponse(adminUser));
    }

    public PlatformResponse reset(UUID id) {
        Platform platform = this.findOne(id);
        platform.setApiKey(UUID.randomUUID().toString().replaceAll("-", ""));
        return new PlatformResponse(platformRepository.save(platform));
    }

    public Page<Platform> findAll(Pageable pageable) {
        return platformRepository.findAll(pageable);
    }

    public Platform findOne(UUID id) {
        return platformRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Platform.class));
    }

    public Platform findOneByApiKey(String apiKey) {
        return platformRepository
                .findByApiKey(apiKey)
                .orElseThrow(() -> new EntityNotFoundException(Platform.class));
    }

    public Platform update(UUID id, Platform platform) {
        Platform existingPlatform = findOne(id);
        if (platform.getName() != null) {
            existingPlatform.setName(platform.getName());
        }

        platformRepository.save(existingPlatform);
        return existingPlatform;
    }

    public void delete(UUID id) {
        try {
            platformRepository.deleteById(id);
            presetDAO.deleteAllByPlatform(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(Platform.class);
        }
    }

    public void deleteOwn() {
        delete(RelationHelper.getPlatformId());
    }

    public Platform findOwned() {
        return platformRepository.findById(RelationHelper.getPlatformId())
                .orElseThrow(() -> new InvalidJwtException("Invalid Jwt Token"));
    }

}
