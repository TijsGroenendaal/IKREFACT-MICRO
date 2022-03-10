package nl.hetckm.bouncer.setting.model;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.platform.model.Platform;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter @Setter
public class Setting {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private int maxDaysLifetime;

    private int maxChallengesLifetime;

    @OneToOne(fetch = FetchType.LAZY)
    private Platform platform;

}
