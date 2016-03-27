package dbsdemo.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Baxos
 */
@Entity
@Table(name="fuels")
public class Fuel implements Serializable {
    
    @Column
    private String fuelType;
    @Column
    private String fuelName;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    public Fuel(){
        // For ORM purposes
    }
    
    public Fuel(String fuelType, String fuelName){
        
        this.fuelType = fuelType;
        this.fuelName = fuelName;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelName() {
        return fuelName;
    }

    public void setFuelName(String fuelName) {
        this.fuelName = fuelName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
