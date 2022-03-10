package nl.hetckm.base.model;


import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.interfaces.WebhookTestable;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter @Setter
public class Verdict implements WebhookTestable {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    @CreatedDate
    private Date createDate = new Date();

    private Boolean approved;
    private String reason;

    @OneToOne(cascade = CascadeType.DETACH)
    private Challenge challenge;

    @Override
    public Object getTestObject() {
       Verdict verdict = new Verdict();
       verdict.setApproved(new Random().nextBoolean());
       verdict.setReason("This is a test verdict webhook.");
       verdict.setId(UUID.randomUUID());
       return new VerdictResponse(verdict);
    }
}
