package dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import dao.generic.GenericDaoAPI;
import entities.FuelType;
import entities.Offer;
import entities.Price;
import misc.MonthlyAverage;

@Remote
public interface PriceDaoAPI extends GenericDaoAPI<Price> {

	public Price getLatestPrice(Offer offer);

	public Date getLatestPrice();

	public List<MonthlyAverage> getAvgMonthPrices(FuelType fuelType);

	public Double getAvgLatest(FuelType fuelType);
	
	public Price getCheapestPrice(FuelType fuelType);
}
