package dbsdemo.sql.custom;

import dbsdemo.entities.FuelType;
import dbsdemo.entities.Offer;
import dbsdemo.entities.Price;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class PriceDao extends GenericDao<Price> {
    
    public PriceDao(){
        super(Price.class);
    }
    
    public Price getLatestPrice(Offer offer){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT id, price, insertion_date, user_id, offer_id "
                + "FROM prices "
                + "WHERE offer_id =:offer_id "
                + "ORDER BY insertion_date DESC LIMIT 1";
        SQLQuery query = session.createSQLQuery(select);
        
        Price result;
        try {
            result = (Price) query
                    .addEntity(Price.class)
                    .setParameter("offer_id", offer.getId())
                    .uniqueResult();
        }
        catch (NullPointerException e){
            // No ratings available for the station
            result = null;
        }
        session.close();
        return result;
    }
    
    public List<MonthlyAverage> getAvgMonthPrices(FuelType fuelType){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT date_part('month',insertion_date), r FROM "
                + "(SELECT insertion_date, CAST(round(CAST(avg(price) AS numeric),3) AS double precision) as r "
                + "FROM prices "
                + "LEFT OUTER JOIN offers o ON prices.offer_id = o.id "
                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
                + "LEFT OUTER JOIN fuel_types ft ON f.fuel_type_id = ft.id "
                + "WHERE CURRENT_DATE - insertion_date <= 365 "
                + "AND ft.id =:fuelTypeId "
                + "GROUP BY insertion_date) sub "
                + "ORDER BY insertion_date ASC";
        SQLQuery query = session.createSQLQuery(select);
        
        List<Object[]> tempResult = query
                .setParameter("fuelTypeId", fuelType.getId())
                .list();
        List<MonthlyAverage> result = new ArrayList<>();
        
        for(int i = 0; i<tempResult.size(); i++){
            result.add(new MonthlyAverage(((Double) tempResult.get(i)[0]).intValue()));
            result.get(i).setMonthAvg((Double) tempResult.get(i)[1]);
        }
        
        session.close();
        return result;
    }
}
