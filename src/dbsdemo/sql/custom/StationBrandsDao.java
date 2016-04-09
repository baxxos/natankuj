package dbsdemo.sql.custom;

import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import dbsdemo.entities.StationBrand;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class StationBrandsDao extends GenericDao<StationBrand>{

    public StationBrandsDao(){
        super(StationBrand.class);
    }
    
    public List<String> getStationBrandsString(){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT brand FROM station_brands ORDER BY brand ASC";
        SQLQuery query = session.createSQLQuery(select);
        
        List<String> result = query.list();
        
        session.close();
        return result;
    }
    
    public StationBrand getStationBrand(String brand){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT DISTINCT * FROM station_brands WHERE brand =:brand";
        SQLQuery query = session.createSQLQuery(select);
        
        StationBrand result = (StationBrand) query
                .addEntity(StationBrand.class)
                .setParameter("brand", brand)
                .uniqueResult();
        
        session.close();
        return result;
    }
}
