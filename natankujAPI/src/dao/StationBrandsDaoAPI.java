package dao;

import java.util.List;
import javax.ejb.Remote;
import dao.generic.GenericDaoAPI;
import entities.StationBrand;

@Remote
public interface StationBrandsDaoAPI extends GenericDaoAPI<StationBrand> {

	public List<String> getStationBrandsString();

	public StationBrand getStationBrand(String brand);
}
