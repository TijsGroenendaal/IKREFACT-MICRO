package nl.hetckm.base.model.bouncer;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter @Setter
public class GpsModel {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID id;

    private String longitudeReference;
    private String latitudeReference;
    private double longitude;
    private double latitude;

    public GpsModel(){

    }
}
