package nl.hetckm.bouncer.setting;

import nl.hetckm.bouncer.setting.model.Setting;
import nl.hetckm.bouncer.setting.model.SettingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setting")
@PreAuthorize("hasAuthority('ADMIN')")
public class SettingController {

    private final SettingService settingService;

    @Autowired
    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping()
    public SettingResponse getSetting() {
        return new SettingResponse(settingService.getSetting());
    }

    @PatchMapping()
    public SettingResponse patchSetting(@RequestBody Setting setting) {
        return new SettingResponse(settingService.patchSetting(setting));
    }

}
