package dbsdemo.sql.custom;

import dbsdemo.sql.GenericDao;
import dbsdemo.entities.Station;
import dbsdemo.entities.User;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Baxos
 */
public class StationDao extends GenericDao<Station> {

    public StationDao(){
        super(Station.class);
    }
    
    public List<Station> getByAttributes(int brand_id, int city_id, double rate){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        // Default select without added conditions. Where 0=0 substitutes for anything
        String select = "SELECT s.id, location, brand_id, city_id "
                + "FROM stations s "
                + "LEFT OUTER JOIN ratings r ON s.id = r.station_id "
                + "WHERE 0=0";
        
        select = (brand_id < 0 ? select : select.concat(" AND brand_id =:brand_id"));
        select = (city_id < 0 ? select : select.concat(" AND city_id =:city_id"));
        select = (rate < 0 ? select : select.concat(" AND r.rate >= :rate_bottom AND r.rate <= :rate_upper"));
        
        SQLQuery query = session.createSQLQuery(select);
        if(brand_id >= 0)
            query.setParameter("brand_id", brand_id);
        if(city_id >= 0)
            query.setParameter("city_id", city_id);
        if(rate >= 0){
            query.setParameter("rate_bottom", rate);
            query.setParameter("rate_upper", rate+1);
        }
        
        List<Station> result = query
                .addEntity(Station.class)
                .list();
        
        session.close();
        return result;
    }
    
    public void updateStation(Station station){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction t = session.beginTransaction();
        String select = "UPDATE stations SET location =:location WHERE id =:id";
        SQLQuery query = session.createSQLQuery(select);
        
        query
                .setParameter("location", station.getLocation())
                .setParameter("id", station.getId())
                .executeUpdate();
        
        t.commit();
        session.close();
    }
}
