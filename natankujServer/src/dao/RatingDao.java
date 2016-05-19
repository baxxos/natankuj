package dao;

import java.math.BigInteger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import dao.generic.GenericDao;
import entities.Rating;
import entities.Station;
import entities.User;

@Stateless
public class RatingDao extends GenericDao<Rating> implements RatingDaoAPI {

	public RatingDao() {
		this.type = Rating.class;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean updateRating(Rating rating) {
		try {
			String select = "UPDATE ratings SET rate =:rate " + 
							"WHERE user_id =:user_id " + 
							"AND station_id =:station_id";
			Query query = em.createNativeQuery(select);
	
			int affectedRows = query.setParameter("rate", rating.getRate())
					.setParameter("user_id", rating.getUser().getId())
					.setParameter("station_id", rating.getStation().getId()).executeUpdate();
	
			return (affectedRows > 0);
		}
		catch (Exception e){
			Logger.getLogger(GenericDao.class.getName())
				.fatal("Failed to perform Hibernate update query", e);
			return false;
		}
	}

	public Double getAverage(Station station) {
		String select = "SELECT CAST(round(CAST(average AS numeric), 1) AS double precision) FROM "
				+ "(SELECT avg(rate) as average FROM ratings r " + "JOIN stations s ON r.station_id = s.id "
				+ "WHERE station_id =:station_id) sub";
		Query query = em.createNativeQuery(select);

		double result;
		try {
			result = (double) query.setParameter("station_id", station.getId()).getResultList().get(0);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			// No ratings available for the station
			return 0.0;
		}
		return result;
	}

	public int getNumberOfRatings(Station station) {
		String select = "SELECT count(*) FROM ratings WHERE station_id =:station_id";
		Query query = em.createNativeQuery(select);
		int result;
		try {
			result = ((BigInteger) query
				.setParameter("station_id", station.getId()).getResultList().get(0))
				.intValue();
		}
		catch (NullPointerException | IndexOutOfBoundsException e) {
			// No ratings available for the station
			return 0;
		}
		return result;
	}

	public int getNumberOfRatings(User user) {

		String select = "SELECT count(*) FROM ratings WHERE user_id =:user_id";
		Query query = em.createNativeQuery(select);
		int result = 0;
		try {
			result = ((BigInteger) query
					.setParameter("user_id", user.getId()).getResultList().get(0)).intValue();
		}
		catch (NullPointerException | IndexOutOfBoundsException e) {
			// No ratings available for the station
			return 0;
		}
		return result;
	}

	public int getNumberOfRatingsTotal() {
		String select = "SELECT count(*) FROM ratings";
		Query query = em.createNativeQuery(select);
		int result = 0;
		try {
			result = ((BigInteger) query.getResultList().get(0)).intValue();
		}
		catch (NullPointerException | IndexOutOfBoundsException e) {
			// No ratings available for the station
			return 0;
		}

		return result;
	}

	public Double getAverage(User user) {
		String select = "SELECT CAST(round(CAST(average AS numeric), 1) AS double precision) FROM "
				+ "(SELECT avg(rate) as average FROM ratings r " + "JOIN users u ON r.user_id = u.id "
				+ "WHERE user_id =:user_id) sub";
		Query query = em.createNativeQuery(select);
		
		double result;
		try {
			result = (double) query.setParameter("user_id", user.getId()).getResultList().get(0);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			// No ratings available for the station
			return 0.0;
		}
		return result;
	}
}
