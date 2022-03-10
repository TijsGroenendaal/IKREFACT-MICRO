package nl.hetckm.bouncer.webhooks.model;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.platform.model.Platform;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * The type Webhook.
 */
@Entity
@Getter @Setter
public class Webhook {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private WebhookType type;

    private String url;

    private String secret;

    private int lastStatusCode;
    private boolean lastError;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Platform platform;

}
