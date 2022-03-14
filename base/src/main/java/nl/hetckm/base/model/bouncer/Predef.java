package nl.hetckm.base.model.bouncer;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter @Setter
public class Predef {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String reason;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Platform platform;
}
