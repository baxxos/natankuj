package dbsdemo.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Baxos
 */
@Entity
@Table(name="fuel_types")
public class FuelType implements Serializable {
    
    @Column(name="fuel_type", unique=true, length=25)
    private String type;
    @OneToMany(mappedBy="type", cascade=CascadeType.REMOVE)
    private List<Fuel> fuels;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    public FuelType(){
        // For ORM purposes
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if(type.contains("95"))
            this.type = "NAT 95";
        else if(type.contains("98"))
            this.type = "NAT 98";
        else if(type.contains("Nafta") || type.contains("Diesel"))
            this.type = "Diesel";
        else
            this.type = "Unknown";
    }

    public List<Fuel> getFuels() {
        return fuels;
    }

    public void setFuels(List<Fuel> fuels) {
        this.fuels = fuels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
