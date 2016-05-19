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
@Table(name = "cities")
public class City implements Serializable {

	@Column(length = 150)
	private String name;
	@Column(length = 150)
	private String region;
	@OneToMany(mappedBy = "city", cascade = CascadeType.REMOVE)
	private List<Station> stations;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public City() {
		// For ORM purposes
	}

	public City(String name, String region) {
		this.name = name;
		this.region = region;
	}

	public City(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}