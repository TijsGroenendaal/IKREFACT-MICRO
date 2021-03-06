package nl.hetckm.bouncer.setting;

import nl.hetckm.base.helper.RelationHelper;
import nl.hetckm.base.model.bouncer.Platform;
import nl.hetckm.base.model.bouncer.Setting;
import nl.hetckm.bouncer.platform.PlatformService;
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
