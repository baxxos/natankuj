package dbsdemo.sql.custom;

import dbsdemo.entities.City;
import dbsdemo.entities.FuelBrand;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
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
    
    public List<String> getFuelBrandsAsString(String fuelType){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT fuel_brand FROM fuels f " +
                        "LEFT OUTER JOIN fuel_brands fb ON f.fuel_brand_id = fb.id " +
                        "LEFT OUTER JOIN fuel_types ft ON f.fuel_type_id = ft.id " +
                        "WHERE fuel_type =:fuelType ORDER BY fuel_brand ASC";
                //SELECT fuel_brand FROM fuel_brands ORDER BY fuel_brand ASC";
        SQLQuery query = session.createSQLQuery(select);
        
        List<String> result = query.setParameter("fuelType", fuelType).list();
        
        session.close();
        return result;
    }
}
