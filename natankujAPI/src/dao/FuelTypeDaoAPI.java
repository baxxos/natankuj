package dao;

import java.util.List;
import javax.ejb.Remote;
import dao.generic.GenericDaoAPI;
import entities.FuelType;

@Remote
public interface FuelTypeDaoAPI extends GenericDaoAPI<FuelType> {
	
	public void insertDefault();

	public List<String> getTypesAsString();

	public FuelType getFuelType(String fuel_type);
}
