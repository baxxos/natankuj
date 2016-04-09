package dbsdemo.sql.custom;

import dbsdemo.entities.City;
import dbsdemo.sql.GenericDao;
import dbsdemo.entities.Station;
import dbsdemo.entities.StationBrand;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Baxos
 */
public class StationDao extends GenericDao<Station> {

    public StationDao(){
        super(Station.class);
    }
    
    public Station getByAttributes(City city, String location, StationBrand brand){

        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT DISTINCT s.id, s.location, s.brand_id, s.city_id "
                + "FROM stations s "
                + "LEFT OUTER JOIN station_brands sb ON s.brand_id = sb.id "
                + "LEFT OUTER JOIN cities c ON s.city_id = c.id "
                + "WHERE s.location =:location "
                + "AND s.brand_id =:brand_id "
                + "AND s.city_id =:city_id";
        SQLQuery query = session.createSQLQuery(select);
        
        List<Station> result = query
                .addEntity(Station.class)
                .setParameter("location", location)
                .setParameter("brand_id", brand.getId())
                .setParameter("city_id", city.getId())
                .list();
        
        session.close();
        return result.size() > 0 ? result.get(0) : null;
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
