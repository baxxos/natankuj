package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name = "stations")
public class Station implements Serializable {

	@ManyToOne
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@Column(length = 150)
	private String location;

	@ManyToOne
	@JoinColumn(name = "brand_id", nullable = false)
	private StationBrand brand;

	@OneToMany(mappedBy = "station", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Rating> ratings;

	@OneToMany(mappedBy = "station", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Offer> offers;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public Station() {
		// For ORM purposes
	}

	public Station(City city, String location, StationBrand stationBrand) {
		this.city = city;
		this.location = location;
		this.brand = stationBrand;
	}

	@Override
	public String toString() {
		return this.brand.getBrand() + " " + this.city + " " + this.location;
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

	public Double getRatings() {
		double sum = 0.0;
		for (Rating rating : this.ratings){
			sum+=rating.getRate();
		}
		return this.ratings.isEmpty() ? 0.0 : sum/this.ratings.size();
	}
	
	public List<Rating> getRatingsList(){
		return this.ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}
}
