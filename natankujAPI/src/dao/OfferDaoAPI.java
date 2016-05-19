package dao;

import java.util.List;

import javax.ejb.Remote;
import dao.generic.GenericDaoAPI;
import entities.Offer;

@Remote
public interface OfferDaoAPI extends GenericDaoAPI<Offer> {

	public List<Offer> getByAttributes(
			int brand_id,
			int city_id,
			int fuel_type_id,
			double rate
	);
	
	public List<Offer> getByAttributes(
			int brand_id,
			int city_id,
			String location,
			int fuel_type_id
	);
}
