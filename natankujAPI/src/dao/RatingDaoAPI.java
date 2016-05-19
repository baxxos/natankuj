package dao;

import javax.ejb.Remote;

import dao.generic.GenericDaoAPI;
import entities.Rating;
import entities.Station;
import entities.User;

@Remote
public interface RatingDaoAPI extends GenericDaoAPI<Rating> {
	public boolean updateRating(Rating rating);

	public Double getAverage(Station station);

	public int getNumberOfRatings(Station station);

	public int getNumberOfRatings(User user);

	public int getNumberOfRatingsTotal();

	public Double getAverage(User user);
}
