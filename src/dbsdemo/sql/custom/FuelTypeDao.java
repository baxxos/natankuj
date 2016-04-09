package dbsdemo.sql.custom;

import dbsdemo.entities.City;
import dbsdemo.entities.FuelType;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Baxos
 */
public class FuelTypeDao extends GenericDao<FuelType> {
    
    public FuelTypeDao(){
        super(FuelType.class);
    }
    
    public void insertDefault(){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction t = session.beginTransaction();
        String sql = "INSERT INTO fuel_types(fuel_type) VALUES "
                + "('Diesel'), "
                + "('NAT 95'), "
                + "('NAT 98');";
        
        SQLQuery query = session.createSQLQuery(sql);        
        query.executeUpdate();
        
        t.commit();
        session.close();
    }
    
    public List<String> getTypesAsString(){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT fuel_type FROM fuel_types ORDER BY fuel_type ASC";
        SQLQuery query = session.createSQLQuery(select);
        
        List<String> result = query.list();
        
        session.close();
        return result;
    }

    public FuelType getFuelType(String fuel_type) {
        
        fuel_type = (fuel_type.contains("95") ? "NAT 95" : fuel_type);
        fuel_type = (fuel_type.contains("98") ? "NAT 98" : fuel_type);
        fuel_type = (fuel_type.contains("Diesel") || fuel_type.contains("Nafta") ? "Diesel" : fuel_type);
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT DISTINCT * FROM fuel_types WHERE fuel_type =:fuel_type";
        SQLQuery query = session.createSQLQuery(select);
        
        FuelType result = (FuelType) query
                .addEntity(FuelType.class)
                .setParameter("fuel_type", fuel_type)
                .uniqueResult();
        
        session.close();
        return result;
    }
}
