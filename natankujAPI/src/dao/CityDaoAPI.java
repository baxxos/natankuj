package dao;

import java.util.List;

import javax.ejb.Remote;

import dao.generic.GenericDaoAPI;
import entities.City;

@Remote
public interface CityDaoAPI extends GenericDaoAPI<City> {

	public City getCity(String name);
	
	public List<String> getCitiesAsString();
}
