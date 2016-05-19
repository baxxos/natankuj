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
@Table(name = "station_brands")
public class StationBrand implements Serializable {
	@Column(length = 50, unique = true)
	private String brand;
	@OneToMany(mappedBy = "brand", cascade = CascadeType.REMOVE)
	private List<Station> stations;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public StationBrand() {
		// For ORM purposes
	}

	public StationBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public String toString() {
		return this.brand;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
}