package dbsdemo.sql.custom;

import dbsdemo.entities.Rating;
import dbsdemo.entities.Station;
import dbsdemo.entities.User;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.math.BigInteger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Baxos
 */
public class RatingDao extends GenericDao {
    
    public RatingDao(){
        super(Rating.class);
    }
    
    public boolean updateRating(Rating rating){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction t = session.beginTransaction();
        String select = "UPDATE ratings SET rate =:rate "
                + "WHERE user_id =:user_id "
                + "AND station_id =:station_id";
        SQLQuery query = session.createSQLQuery(select);
        
        int affectedRows = query
                .setParameter("rate", rating.getRate())
                .setParameter("user_id", rating.getUser().getId())
                .setParameter("station_id", rating.getStation().getId())
                .executeUpdate();
        
        t.commit();
        session.close();
        return (affectedRows > 0);
    }
    
    public Double getAverage(Station station){

        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT CAST(round(CAST(average AS numeric), 1) AS double precision) FROM "
                + "(SELECT avg(rate) as average FROM ratings r "
                + "JOIN stations s ON r.station_id = s.id "
                + "WHERE station_id =:station_id) sub";
        SQLQuery query = session.createSQLQuery(select);
        
        double result;
        try {
            result = (double) query
                    .setParameter("station_id", station.getId())
                    .uniqueResult();
        }
        catch (NullPointerException e){
            // No ratings available for the station
            result = 0.0;
        }
        session.close();
        return  result;
    }
    
    public int getNumberOfRatings(Station station){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT count(*) FROM ratings WHERE station_id =:station_id";
        SQLQuery query = session.createSQLQuery(select);
        
        int result = ((BigInteger) query
                .setParameter("station_id", station.getId())
                .uniqueResult())
                .intValue();
        
        session.close();
        return result;
    }
    
    public int getNumberOfRatings(User user){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT count(*) FROM ratings WHERE user_id =:user_id";
        SQLQuery query = session.createSQLQuery(select);
        
        int result = ((BigInteger) query
                .setParameter("user_id", user.getId())
                .uniqueResult())
                .intValue();
        
        session.close();
        return result;
    }
    
    public int getNumberOfRatingsTotal(){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT count(*) FROM ratings";
        SQLQuery query = session.createSQLQuery(select);
        
        int result = ((BigInteger) query
                .uniqueResult())
                .intValue();
        
        session.close();
        return result;
    }
    
    public Double getAverage(User user){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT CAST(round(CAST(average AS numeric), 1) AS double precision) FROM "
                + "(SELECT avg(rate) as average FROM ratings r "
                + "JOIN users u ON r.user_id = u.id "
                + "WHERE user_id =:user_id) sub";
        SQLQuery query = session.createSQLQuery(select);
        
        double result;
        try {
            result = (double) query
                    .setParameter("user_id", user.getId())
                    .uniqueResult();
        }
        catch (NullPointerException e){
            // No ratings available for the station
            result = 0.0;
        }
        session.close();
        return  result;
    }
}
