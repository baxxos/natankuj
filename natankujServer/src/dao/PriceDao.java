package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import dao.generic.GenericDao;
import entities.FuelType;
import entities.Offer;
import entities.Price;
import misc.MonthlyAverage;

@Stateless
public class PriceDao extends GenericDao<Price> implements PriceDaoAPI {

	public PriceDao(){
		this.type = Price.class;
	}
	
	@Override
	public Price getLatestPrice(Offer offer){
        String select = "SELECT id, price, insertion_date, user_id, offer_id "
                + "FROM prices "
                + "WHERE offer_id =:offer_id "
                + "ORDER BY insertion_date DESC LIMIT 1";
        Query query = this.em.createNativeQuery(select, Price.class);
        
        List<Price> result;
        try {
            result = query
                    .setParameter("offer_id", offer.getId())
                    .getResultList();
            
            for(Price price : result){
            	em.detach(price);
            }
        }
        catch (Exception e){
            // No offers available
        	Logger.getLogger(StationDao.class.getName())
				.warn("No offer available for specified parameters", e);
            return null;
        }
        return result.isEmpty() ? null : result.get(0);
    }

	@Override
	public Date getLatestPrice() {
        String select = "SELECT id, price, insertion_date, user_id, offer_id "
                + "FROM prices ORDER BY insertion_date DESC LIMIT 1";
        Query query = em.createNativeQuery(select, Price.class);
        
        List<Price> result;
        try {
            result = query.getResultList();
            
            for(Price price : result){
            	em.detach(price);
            }
        }
        catch (Exception e){
            // No results available
        	Logger.getLogger(PriceDao.class.getName())
				.warn("Unknown exception while getting latest price date", e);
            return null;
        }
        return result.isEmpty() ? null : result.get(0).getDate();
	}

	@Override
	public List<MonthlyAverage> getAvgMonthPrices(FuelType fuelType) {
		List<MonthlyAverage> result = new ArrayList<>();
		
		try {
	        String select = "SELECT dp, r FROM "
	                + "(SELECT date_part('month', insertion_date) as dp, "
	                	+ "date_part('year', insertion_date) as y, "
	                	+ "CAST(round(CAST(avg(price) AS numeric),3) AS double precision) as r "
	                + "FROM prices "
	                + "LEFT OUTER JOIN offers o ON prices.offer_id = o.id "
	                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
	                + "LEFT OUTER JOIN fuel_types ft ON f.fuel_type_id = ft.id "
	                + "WHERE CURRENT_DATE - insertion_date <= 365 "
	                + "AND ft.id =:fuelTypeId "
	                + "GROUP BY dp, y "
	                + "ORDER BY y, dp ASC) sub ";
	        Query query = this.em.createNativeQuery(select);
	        
	        List<Object[]> tempResult = query
	                .setParameter("fuelTypeId", fuelType.getId())
	                .getResultList();
	        
	        for(int i = 0; i<tempResult.size(); i++){
	            result.add(new MonthlyAverage(((Double) tempResult.get(i)[0]).intValue()));
	            result.get(i).setMonthAvg((Double) tempResult.get(i)[1]);
	            Logger.getLogger(PriceDao.class.getName())
					.debug(result.get(i).getMonthName()+": "+result.get(i).getMonthAvg());
	        }
		}
		catch (Exception e){
			Logger.getLogger(PriceDao.class.getName())
				.warn("Failed to get monthly average prices from the database", e);
		}
        return result;
	}

	@Override
	public Double getAvgLatest(FuelType fuelType) {
        List<Double> result = new ArrayList<>();
        
        String select = "SELECT CAST(round(CAST(avg(ag) AS numeric),3) AS double precision) FROM "
                + "(SELECT avg(price) as ag, fuel_brand_id FROM "
                + "(SELECT * FROM prices p "
                + "LEFT OUTER JOIN offers o ON p.offer_id = o.id "
                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
                + "LEFT OUTER JOIN fuel_types ft ON f.fuel_type_id = ft.id "
                + "WHERE fuel_type =:fuelType "
                + "ORDER BY insertion_date DESC LIMIT 10) sub "
                + "GROUP BY fuel_brand_id ) sub2 ";
        try {
        	Query query = this.em.createNativeQuery(select);
            result = query
                    .setParameter("fuelType", fuelType.getType())
                    .getResultList();
        }
        catch(Exception e){
        	Logger.getLogger(PriceDao.class.getName())
				.warn("Failed to get latest average price from the database", e);
        }

        return result.isEmpty() ? 0.0 : result.get(0);
	}

	@Override
	public Price getCheapestPrice(FuelType fuelType) {
        String select = "SELECT * FROM prices p "
                + "LEFT OUTER JOIN offers o ON p.offer_id = o.id "
                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
                + "WHERE f.fuel_type_id =:fuel_type_id "
                + "ORDER BY price ASC LIMIT 1 ";
        List<Price> result = new ArrayList<>();
        try {
        	Query query = this.em.createNativeQuery(select, Price.class);
            result = query
                    .setParameter("fuel_type_id", fuelType.getId())
                    .getResultList();
        }
        catch (Exception e){
        	Logger.getLogger(PriceDao.class.getName())
				.warn("Failed to get cheapes price from database", e);
        }
        return result.isEmpty() ? null : result.get(0);
	}

}
