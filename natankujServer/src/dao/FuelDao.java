package dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import dao.generic.GenericDao;
import entities.Fuel;
import entities.FuelBrand;
import entities.FuelType;

@Stateless
public class FuelDao extends GenericDao<Fuel> implements FuelDaoAPI {

	@Override
	public Fuel getByAttributes(FuelBrand fuel_brand, FuelType fuel_type) {
        String select = "SELECT DISTINCT * FROM fuels "
                + "WHERE fuel_brand_id =:fuel_brand "
                + "AND fuel_type_id =:fuel_type ";
        
        List<Fuel> result = new ArrayList<>();
        try {
	        Query query = this.em.createNativeQuery(select, Fuel.class);
	        
	        result = query
	                .setParameter("fuel_brand", fuel_brand.getId())
	                .setParameter("fuel_type", fuel_type.getId())
	                .getResultList();
	        
	        for(Fuel fuel : result){
	        	em.detach(fuel);
	        }
        }
        catch (Exception e){
        	Logger.getLogger(FuelDao.class)
        		.fatal("Failed to get fuels by attributes from database", e);
        }
        return result.isEmpty() ? null : result.get(0);
	}

}
