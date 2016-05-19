package dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import dao.generic.GenericDao;
import entities.FuelType;

@Stateless
public class FuelTypeDao extends GenericDao<FuelType> implements FuelTypeDaoAPI {

	public FuelTypeDao(){
		this.type = FuelType.class;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void insertDefault() {
		try {
	        String sql = "INSERT INTO fuel_types(fuel_type) VALUES "
	                + "('Diesel'), "
	                + "('NAT 95'), "
	                + "('NAT 98');";
	        
	        this.em.createNativeQuery(sql).executeUpdate();
		}
		catch (Exception e) {
			Logger.getLogger(FuelTypeDao.class.getName())
				.fatal("Failed to insert default fuelType values", e);
		}
	}

	@Override
	public List<String> getTypesAsString() {
        String select = "SELECT fuel_type FROM fuel_types ORDER BY fuel_type ASC";
        Query query = this.em.createNativeQuery(select);
        
        List<String> result = new ArrayList<>();
        try {
        	result = query.getResultList();
        }
        catch (Exception e) {
        	Logger.getLogger(FuelTypeDao.class.getName())
				.fatal("Failed to get fuel types as string value", e);
        }
        return result;
	}

	@Override
	public FuelType getFuelType(String fuel_type) {
		// Determine real fuel type 
        fuel_type = (fuel_type.contains("95") ? "NAT 95" : fuel_type);
        fuel_type = (fuel_type.contains("98") ? "NAT 98" : fuel_type);
        fuel_type = (fuel_type.contains("Diesel") || fuel_type.contains("Nafta") ? "Diesel" : fuel_type);
        
        String select = "SELECT DISTINCT * FROM fuel_types WHERE fuel_type =:fuel_type";
        List<FuelType> result = new ArrayList<>();
        try {
	        Query query = this.em.createNativeQuery(select, FuelType.class);
	        
	        result = query
	                .setParameter("fuel_type", fuel_type)
	                .getResultList();
	        
	        for (FuelType ft : result){
	        	this.em.detach(ft);
	        }
        }
        catch (Exception e){ 
        	
        }
        return result.isEmpty() ? null : result.get(0);
	}
}
