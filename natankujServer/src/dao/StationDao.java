package dao;

import java.math.BigInteger;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import dao.generic.GenericDao;
import entities.City;
import entities.Station;
import entities.StationBrand;

@Stateless
public class StationDao extends GenericDao<Station> implements StationDaoAPI {
	
	public StationDao() {
		this.type = Station.class;
	}
	
	@Override
	public Station getByAttributes(City city, String location, StationBrand brand) {
		String select = "SELECT DISTINCT s.id, s.location, s.brand_id, s.city_id " + "FROM stations s "
				+ "LEFT OUTER JOIN station_brands sb ON s.brand_id = sb.id "
				+ "LEFT OUTER JOIN cities c ON s.city_id = c.id " + "WHERE s.location =:location "
				+ "AND s.brand_id =:brand_id " + "AND s.city_id =:city_id";
		Query query = em.createNativeQuery(select, Station.class);

		List<Station> result = query.setParameter("location", location)
				.setParameter("brand_id", brand.getId()).setParameter("city_id", city.getId())
				.getResultList();
		
		for (Station station : result){
			this.em.detach(station);
		}

		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateStation(Station station) {
		String select = "UPDATE stations SET location =:location WHERE id =:id";
		Query query = em.createNativeQuery(select);

		query.setParameter("location", station.getLocation())
			.setParameter("id", station.getId()).executeUpdate();
	}

	@Override
	public int getNumberOfStations() {
		String select = "SELECT count(*) FROM stations";
		Query query = em.createNativeQuery(select);

		int result;
		try {
			result = ((BigInteger) query.getResultList().get(0)).intValue();
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			// No ratings currently available for the station
			Logger.getLogger(StationDao.class.getName())
				.warn("No ratings currently available for stations", e);
			return 0;
		}
		return result;
	}
}
