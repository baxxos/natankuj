package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="fuels")
public class Fuel implements Serializable {
    
    @ManyToOne
    @JoinColumn(name="fuel_brand_id")
    private FuelBrand brand;
    @ManyToOne
    @JoinColumn(name="fuel_type_id")
    private FuelType type;
    @OneToMany(mappedBy="fuel", fetch = FetchType.EAGER, cascade=CascadeType.REMOVE)
    private List<Offer> offers;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    public Fuel(){
        // For ORM purposes
    }
    
    public Fuel(FuelBrand brand, FuelType type){
        this.brand = brand;
        this.type = type;
    }
    
    public FuelBrand getBrand() {
        return brand;
    }

    public void setBrand(FuelBrand brand) {
        this.brand = brand;
    }

    public FuelType getType() {
        return type;
    }

    public void setType(FuelType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
