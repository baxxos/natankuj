package dbsdemo.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Baxos
 */
@Entity
@Table(name="offers")
public class Offer implements Serializable {
    
    @ManyToOne
    @JoinColumn(name="station_id", nullable=false)
    private Station station;
    @ManyToOne
    @JoinColumn(name="fuel_id", nullable=false)
    private Fuel fuel;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    public Offer(){
        // For ORM purposes
    }
}
