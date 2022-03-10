package nl.hetckm.bouncer.setting;

import nl.hetckm.base.model.Platform;
import nl.hetckm.base.model.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SettingRepository extends CrudRepository<Setting, UUID> {
    Setting findByPlatform(Platform platform);
}
