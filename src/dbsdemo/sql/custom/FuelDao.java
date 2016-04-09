
package dbsdemo.sql.custom;

import dbsdemo.entities.Fuel;
import dbsdemo.entities.FuelBrand;
import dbsdemo.entities.FuelType;
import dbsdemo.entities.Station;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class FuelDao extends GenericDao<Fuel> {
    
    public FuelDao(){
        super(Fuel.class);
    }
    
    public Fuel getByAttributes(FuelBrand fuel_brand, FuelType fuel_type){
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT DISTINCT * FROM fuels "
                + "WHERE fuel_brand_id =:fuel_brand "
                + "AND fuel_type_id =:fuel_type ";
        SQLQuery query = session.createSQLQuery(select);
        
        Fuel result = (Fuel) query
                .addEntity(Fuel.class)
                .setParameter("fuel_brand", fuel_brand.getId())
                .setParameter("fuel_type", fuel_type.getId())
                .uniqueResult();
        
        session.close();
        return result;
    }
}
