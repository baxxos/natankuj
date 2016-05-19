package dao;

import javax.ejb.Remote;
import dao.generic.GenericDaoAPI;
import entities.Fuel;
import entities.FuelBrand;
import entities.FuelType;

@Remote
public interface FuelDaoAPI extends GenericDaoAPI<Fuel> {

	public Fuel getByAttributes(FuelBrand fuel_brand, FuelType fuel_type);
}
