package dao;

import java.util.List;

import javax.ejb.Remote;

import dao.generic.GenericDaoAPI;
import entities.FuelBrand;

@Remote
public interface FuelBrandDaoAPI extends GenericDaoAPI<FuelBrand> {

	public FuelBrand getFuelBrand(String fuel_brand);
	
	public List<String> getFuelBrandsAsString(String fuelType);
}
