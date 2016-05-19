package dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import dao.generic.GenericDao;
import entities.Offer;

@Stateless
public class OfferDao extends GenericDao<Offer> implements OfferDaoAPI {

	public OfferDao(){
		this.type = Offer.class;
	}

	@Override
	public List<Offer> getByAttributes(int brand_id, int city_id, int fuel_type_id, double rate) {
        // Default select without added conditions. Where 0=0 substitutes for anything
        String select = "SELECT DISTINCT o.id, o.station_id, o.fuel_id FROM offers o "
                + "LEFT OUTER JOIN stations s ON o.station_id = s.id "
                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
                + "LEFT OUTER JOIN ratings r ON s.id = r.station_id "
                + "WHERE 0=0";

        select = (brand_id < 0 ? select : select.concat(" AND s.brand_id =:brand_id"));
        select = (fuel_type_id < 0 ? select : select.concat(" AND f.fuel_type_id =:fuel_type_id"));
        select = (city_id < 0 ? select : select.concat(" AND s.city_id =:city_id"));
        select = (rate < 0 ? select : select.concat(" AND r.rate >= :rate_bottom AND r.rate <= :rate_upper"));
        
        // Create a query with variable number of parameters
        Query query = this.em.createNativeQuery(select, Offer.class);
        if(brand_id >= 0)
            query.setParameter("brand_id", brand_id);
        if(city_id >= 0)
            query.setParameter("city_id", city_id);
        if(fuel_type_id > 0)
            query.setParameter("fuel_type_id", fuel_type_id);
        if(rate >= 0){
            query.setParameter("rate_bottom", rate);
            query.setParameter("rate_upper", rate+1);
        }
        
        // Try to get result list of offers based on given parameters
        List<Offer> result = new ArrayList<>();
        try {
	        result = query.getResultList();
	        
	        for(Offer offer : result){
	        	em.detach(offer);
	        }
        }
        catch (Exception e) {
        	Logger.getLogger(OfferDao.class.getName())
				.fatal("Failed to properly retrieve list of offers from database", e);
        }
        return result;
    }
	
	@Override
	public List<Offer> getByAttributes(int brand_id, int city_id, String location, int fuel_type_id) {
        // Default select without added conditions. Where 0=0 substitutes for anything
        String select = "SELECT DISTINCT o.id, o.station_id, o.fuel_id FROM offers o "
                + "LEFT OUTER JOIN stations s ON o.station_id = s.id "
                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
                + "WHERE 0=0";

        select = (brand_id < 0 ? select : select.concat(" AND s.brand_id =:brand_id"));
        select = (fuel_type_id < 0 ? select : select.concat(" AND f.fuel_type_id =:fuel_type_id"));
        select = (city_id < 0 ? select : select.concat(" AND s.city_id =:city_id"));
        select = (location.isEmpty() ? select : select.concat(" AND s.location =:location"));
        
        // Create a query with variable number of parameters
        Query query = this.em.createNativeQuery(select, Offer.class);
        if(brand_id >= 0)
            query.setParameter("brand_id", brand_id);
        if(city_id >= 0)
            query.setParameter("city_id", city_id);
        if(fuel_type_id > 0)
            query.setParameter("fuel_type_id", fuel_type_id);
        if(!location.isEmpty()){
            query.setParameter("location", location);
        }
        
        // Try to get result list of offers based on given parameters
        List<Offer> result = new ArrayList<>();
        try {
	        result = query.getResultList();
	        
	        for(Offer offer : result){
	        	em.detach(offer);
	        }
        }
        catch (Exception e) {
        	Logger.getLogger(OfferDao.class.getName())
				.fatal("Failed to properly retrieve list of offers from database", e);
        }
        return result;
    }
}
