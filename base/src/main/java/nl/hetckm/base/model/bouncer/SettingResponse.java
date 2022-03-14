package nl.hetckm.base.model.bouncer;

import lombok.Value;

@Value
public class SettingResponse {
    int maxDaysLifetime;
    int maxChallengesLifetime;

    public SettingResponse(Setting setting) {
        this.maxChallengesLifetime = setting.getMaxChallengesLifetime();
        this.maxDaysLifetime = setting.getMaxDaysLifetime();
    }
}
