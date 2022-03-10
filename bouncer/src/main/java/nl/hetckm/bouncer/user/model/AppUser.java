package nl.hetckm.bouncer.user.model;

import lombok.Getter;
import lombok.Setter;
import nl.hetckm.bouncer.platform.model.Platform;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter @Setter
public class AppUser {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = true;

    @CreatedDate
    private Date createDate = new Date();

    @ManyToOne(cascade = CascadeType.DETACH)
    private Platform platform;
}
