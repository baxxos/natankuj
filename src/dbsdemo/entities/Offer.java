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

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
    
    public String getBrand(){
        return this.station.getBrand().getBrand();
    }
    
    public String getLocation(){
        return this.station.getLocation();
    }
    
    public String getCity(){
        return this.station.getCity().getName();
    }
    
    public Double getRatings(){
        return this.station.getRatings();
    }
    
    public Fuel getFuel() {
        return fuel;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }
    
    public String getFuelType(){
        return this.fuel.getType().getType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
