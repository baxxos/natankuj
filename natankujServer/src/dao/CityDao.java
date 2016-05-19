package dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import dao.generic.GenericDao;
import entities.City;

@Stateless
public class CityDao extends GenericDao<City> implements CityDaoAPI {

	public CityDao(){
		this.type = City.class;
	}
	
	@Override
	public City getCity(String name) {
        String select = "SELECT DISTINCT * FROM cities WHERE name =:name";
        List<City> result = new ArrayList<>();
        
        try {
	        Query query = this.em.createNativeQuery(select, City.class);
	        result = query
	                .setParameter("name", name)
	                .getResultList();
	        
	        for(City city : result){
	        	this.em.detach(city);
	        }
        }
        catch (Exception e) {
        	Logger.getLogger(CityDao.class)
        		.fatal("Failed to obtain specified city ("+name+") from database", e);
        }
        
        return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public List<String> getCitiesAsString() {
        String select = "SELECT name FROM cities ORDER BY name ASC";
        List<String> result = new ArrayList<>();
        
        try {
	        Query query = this.em.createNativeQuery(select);
	        result = query.getResultList();
        }
        catch (Exception e) {
        	Logger.getLogger(CityDao.class)
    			.fatal("Failed to obtain list of cities as strings from database", e);
        }
        return result;
	}

}
