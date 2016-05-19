package dao;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import dao.generic.GenericDao;
import entities.FuelBrand;

@Stateless
public class FuelBrandDao extends GenericDao<FuelBrand> implements FuelBrandDaoAPI {

	public FuelBrandDao(){
		this.type = FuelBrand.class;
	}
	
	@Override
	public FuelBrand getFuelBrand(String fuel_brand) {
        String select = "SELECT DISTINCT * FROM fuel_brands WHERE fuel_brand =:fuel_brand";
        
        List<FuelBrand> result = new ArrayList<>();
        try {
	        Query query = this.em.createNativeQuery(select, FuelBrand.class);
	        result = query
	                .setParameter("fuel_brand", fuel_brand)
	                .getResultList();
	        
	        for(FuelBrand fb : result){
	        	em.detach(fb);
	        }
        }
        catch (Exception e){
        	Logger.getLogger(FuelBrand.class)
        		.fatal("Failed to obtain fuel brand from database", e);
        }
        return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public List<String> getFuelBrandsAsString(String fuelType) {
		String select = "SELECT fuel_brand FROM fuels f "
				+ "LEFT OUTER JOIN fuel_brands fb ON f.fuel_brand_id = fb.id "
				+ "LEFT OUTER JOIN fuel_types ft ON f.fuel_type_id = ft.id "
				+ "WHERE fuel_type =:fuelType ORDER BY fuel_brand ASC";
		
		List<String> result = new ArrayList<>();
		try {
			Query query = this.em.createNativeQuery(select);
			result = query.setParameter("fuelType", fuelType).getResultList();
		}
		catch (Exception e) {
			Logger.getLogger(FuelBrand.class)
			.fatal("Failed to obtain fuel brand list as string from the database", e);
		}
		return result;
	}

	
}
