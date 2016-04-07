package dbsdemo.sql.custom;

import dbsdemo.sql.GenericDao;
import dbsdemo.entities.Station;
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
