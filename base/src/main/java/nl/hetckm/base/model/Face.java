package nl.hetckm.base.model;

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
public class Face implements VertexContainer {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonBackReference(value = "media-face")
    private Media media;

    @OneToMany(mappedBy = "face", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "face-vertex")
    private Set<Vertex> vertexes;

}
