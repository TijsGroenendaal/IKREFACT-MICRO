package nl.hetckm.base.model.bouncer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter @Setter
public class WebPageMatch {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String title;
    private String url;
    private int fullMatches;
    private int partialMatches;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "media-webpage")
    private Media media;

}
