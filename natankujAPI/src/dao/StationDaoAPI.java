package dao;

import javax.ejb.Remote;

import dao.generic.GenericDaoAPI;
import entities.City;
import entities.Station;
import entities.StationBrand;

@Remote
public interface StationDaoAPI extends GenericDaoAPI<Station> {

	public Station getByAttributes(City city, String location, StationBrand brand);

	public void updateStation(Station station);
	
	public int getNumberOfStations();
}
