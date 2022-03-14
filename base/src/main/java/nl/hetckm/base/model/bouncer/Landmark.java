package nl.hetckm.base.model.bouncer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import nl.hetckm.base.interfaces.VertexContainer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter
public class Landmark implements VertexContainer {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String description;
    private String longitude;
    private String latitude;

    @OneToMany(mappedBy = "landmark", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "landmark-vertex")
    private Set<Vertex> vertexes;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "media-landmark")
    private Media media;

}
