package dbsdemo.sql.custom;

import dbsdemo.entities.City;
import dbsdemo.entities.StationBrand;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class CityDao extends GenericDao<City> {
    
    public CityDao(){
        super(City.class);
    }
    
    public List<City> getCity(String name){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT * FROM cities WHERE name =:name";
        SQLQuery query = session.createSQLQuery(select);
        
        List<City> result = query
                .addEntity(City.class)
                .setParameter("name", name)
                .list();
        
        session.close();
        return result;
    }
    
    public List<String> getCitiesAsString(){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT name FROM cities ORDER BY name ASC";
        SQLQuery query = session.createSQLQuery(select);
        
        List<String> result = query.list();
        
        session.close();
        return result;
    }
}
