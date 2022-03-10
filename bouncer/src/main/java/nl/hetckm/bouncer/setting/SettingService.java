package nl.hetckm.bouncer.setting;

import nl.hetckm.bouncer.helper.RelationHelper;
import nl.hetckm.bouncer.platform.PlatformService;
import nl.hetckm.bouncer.platform.model.Platform;
import nl.hetckm.bouncer.setting.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

    private final SettingRepository settingRepository;
    private final PlatformService platformService;

    @Autowired
    public SettingService(SettingRepository settingRepository, PlatformService platformService) {
        this.settingRepository = settingRepository;
        this.platformService = platformService;
    }

    public Setting getSetting() {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        return settingRepository.findByPlatform(platform);
    }

    public Setting patchSetting(Setting setting) {
        Platform platform = platformService.findOne(RelationHelper.getPlatformId());
        Setting oldSetting = settingRepository.findByPlatform(platform);
        oldSetting.setMaxChallengesLifetime(setting.getMaxChallengesLifetime());
        oldSetting.setMaxDaysLifetime(setting.getMaxDaysLifetime());
        return settingRepository.save(oldSetting);
    }
}
