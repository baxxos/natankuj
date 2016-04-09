package dbsdemo.sql.custom;

import dbsdemo.entities.City;
import dbsdemo.entities.FuelBrand;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class FuelBrandDao extends GenericDao<FuelBrand> {
    
    public FuelBrandDao(){
        super(FuelBrand.class);
    }
    
    public FuelBrand getFuelBrand(String fuel_brand){
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT DISTINCT * FROM fuel_brands WHERE fuel_brand =:fuel_brand";
        SQLQuery query = session.createSQLQuery(select);
        
        FuelBrand result = (FuelBrand) query
                .addEntity(FuelBrand.class)
                .setParameter("fuel_brand", fuel_brand)
                .uniqueResult();
        
        session.close();
        return result;
    }
}
