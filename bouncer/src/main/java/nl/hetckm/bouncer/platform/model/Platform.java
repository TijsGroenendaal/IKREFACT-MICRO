package nl.hetckm.bouncer.platform.model;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.predefs.model.Predef;
import nl.hetckm.bouncer.preset.model.Preset;
import nl.hetckm.bouncer.setting.model.Setting;
import nl.hetckm.bouncer.user.model.AppUser;
import nl.hetckm.bouncer.verification.model.Verification;
import nl.hetckm.bouncer.webhooks.model.Webhook;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter
public class Platform {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String name;

    private String apiKey;

    private String encryptionKey;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Verification> verifications;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Preset> presets;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppUser> users;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Webhook> webhooks;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Predef> predefs;

    @OneToOne(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Setting setting;
}
