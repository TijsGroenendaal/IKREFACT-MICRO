package nl.hetckm.base.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter @Setter
public class Vertex {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private double x;
    private double y;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "face-vertex")
    private Face face;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "landmark-vertex")
    private Landmark landmark;

}
