package nl.hetckm.base.model.bouncer;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.enums.VerificationStatus;
import nl.hetckm.base.interfaces.Reviewable;
import nl.hetckm.base.interfaces.WebhookTestable;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter
public class Verification implements WebhookTestable, Reviewable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    @CreatedDate
    private Date createDate = new Date();

    private String name;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    private int maxChallengeLifetime;

    @OneToMany(mappedBy = "verification", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Challenge> challenges;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Platform platform;

    @Override
    public Object getTestObject() {
        Verification verification = new Verification();
        verification.setId(UUID.randomUUID());
        verification.setCreateDate(new Date());
        verification.setStatus(new Random().nextBoolean() ? VerificationStatus.ACCEPTED : VerificationStatus.REJECTED);
        return new VerificationResponse(verification);
    }
}
