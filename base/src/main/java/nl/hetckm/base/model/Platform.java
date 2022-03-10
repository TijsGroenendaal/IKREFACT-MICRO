package nl.hetckm.base.model;

import lombok.Getter;
import lombok.Setter;
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
