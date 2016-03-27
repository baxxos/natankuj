package dbsdemo.entities;

import java.io.Serializable;
import javax.persistence.Column;
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
@Table(name="stations")
public class Station implements Serializable {
    
    @ManyToOne
    @JoinColumn(name="city_id", nullable=false)
    private City city;
    @Column(length=150)
    private String location;
    @ManyToOne
    @JoinColumn(name="brand_id", nullable=false)
    private StationBrand brand;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    public Station(){
        // For ORM purposes
    }
    
    public Station(City city, String location, StationBrand stationBrand){
        this.city = city;
        this.location = location;
        this.brand = stationBrand;
    }
    
    @Override
    public String toString(){
        return this.brand.getBrand()+" "+this.city+" "+this.location;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StationBrand getBrand() {
        return brand;
    }

    public void setBrand(StationBrand brand) {
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
