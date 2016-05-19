package entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@Table(name = "offers")
public class Offer implements Serializable {

	@ManyToOne
	@JoinColumn(name = "station_id", nullable = false)
	private Station station;
	@ManyToOne
	@JoinColumn(name = "fuel_id", nullable = false)
	private Fuel fuel;
	@OneToMany(mappedBy = "offer", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Price> prices;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public Offer() {
		// For ORM purposes
	}

	public Offer(Station station, Fuel fuel) {
		this.station = station;
		this.fuel = fuel;
	}

	@Override
	public String toString() {
		return (this.getBrand() + "\n" + this.getCity() + "\n" + this.getLocation() + "\n\n" + "Hodnotenie: "
				+ this.getRatings() + " (hlasov: " + this.station.getRatingsList().size() + ")\n"
				+ this.fuel.getBrand().getBrand() + "\n" + this.fuel.getBrand().getDescription());
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getBrand() {
		return this.station.getBrand().getBrand();
	}

	public String getLocation() {
		return this.station.getLocation();
	}

	public String getCity() {
		return this.station.getCity().getName();
	}

	public Double getPrice() {

		Date recent = new Date(Integer.MIN_VALUE);
		Price result = null;
		for (Price price : this.prices) {
			if (price.getDate().after(recent)) {
				recent = price.getDate();
				result = price;
			}
		}

		return result == null ? 0.0 : result.getPrice();
	}

	public String getDate() {

		Date recent = new Date(Integer.MIN_VALUE);
		Price result = null;
		for (Price price : this.prices) {
			if (price.getDate().after(recent)) {
				recent = price.getDate();
				result = price;
			}
		}

		return result == null ? "-" : new SimpleDateFormat("dd.MM.yyyy").format(result.getDate());
	}

	public Double getRatings() {
		return this.station.getRatings();
	}

	public Fuel getFuel() {
		return fuel;
	}

	public void setFuel(Fuel fuel) {
		this.fuel = fuel;
	}

	public String getFuelType() {
		return this.fuel.getType().getType();
	}

	public String getFuelBrand() {
		return this.fuel.getBrand().getBrand();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addPrice(Price price) {
		this.prices.add(price);
	}
}