package entities;

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

@Entity
@Table(name = "fuel_brands")
public class FuelBrand implements Serializable {

	@Column(name = "fuel_brand", unique = true, length = 50)
	private String brand;
	@Column
	private String description;
	@OneToMany(mappedBy = "brand", cascade = CascadeType.REMOVE)
	private List<Fuel> fuels;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public FuelBrand() {
		// For ORM purposes
	}

	public FuelBrand(String brand) {
		this.brand = brand;
		this.description = "Description not available";
	}

	public FuelBrand(String brand, String description) {
		this.brand = brand;
		this.description = description;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
