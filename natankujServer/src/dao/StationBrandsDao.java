package dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import dao.generic.GenericDao;
import entities.StationBrand;

@Stateless
public class StationBrandsDao extends GenericDao<StationBrand> implements StationBrandsDaoAPI {

	public StationBrandsDao(){
		this.type = StationBrand.class;
	}
	
	@Override
	public List<String> getStationBrandsString() {
        String select = "SELECT brand FROM station_brands ORDER BY brand ASC";
        List<String> result = new ArrayList<>();
        try {
        Query query = this.em.createNativeQuery(select);
        
        result = (List<String>) query.getResultList();
        }
        catch (Exception e){
        	Logger.getLogger(StationBrandsDao.class.getName())
				.fatal("Failed to perform Hibernate get_as_string query", e);
        }
        return result;
	}

	@Override
	public StationBrand getStationBrand(String brand) {
        String select = "SELECT DISTINCT * FROM station_brands WHERE brand =:brand";
        List<StationBrand> result = new ArrayList<>();
        try {
	        Query query = this.em.createNativeQuery(select, StationBrand.class);
	        result = (List<StationBrand>) query
	                .setParameter("brand", brand)
	                .getResultList();
	        
	        for(StationBrand sb : result){
	        	em.detach(sb);
	        }
		}
        catch (Exception e){
        	Logger.getLogger(StationBrandsDao.class.getName())
				.fatal("Failed to perform Hibernate getStationBrand query", e);
        }
        return result.isEmpty() ? null : result.get(0);
	}
}
