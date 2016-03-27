package dbsdemo.sql.custom;

import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import dbsdemo.entities.StationBrand;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
    
    public List<StationBrand> getStationBrand(String brand){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT * FROM station_brands WHERE brand =:brand";
        SQLQuery query = session.createSQLQuery(select);
        
        List<StationBrand> result = query
                .addEntity(StationBrand.class)
                .setParameter("brand", brand)
                .list();
        
        session.close();
        return result;
    }
}
