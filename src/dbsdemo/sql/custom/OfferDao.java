package dbsdemo.sql.custom;

import dbsdemo.entities.Offer;
import dbsdemo.entities.Station;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class OfferDao extends GenericDao<Offer> {

    public OfferDao(){
        super(Offer.class);
    }
    
    public List<Offer> getByAttributes(int brand_id, int city_id, int fuel_type_id, double rate){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        // Default select without added conditions. Where 0=0 substitutes for anything
        String select = "SELECT DISTINCT o.id, o.station_id, o.fuel_id FROM offers o "
                + "LEFT OUTER JOIN stations s ON o.station_id = s.id "
                + "LEFT OUTER JOIN fuels f ON o.fuel_id = f.id "
                + "LEFT OUTER JOIN ratings r ON s.id = r.station_id "
                + "WHERE 0=0";

        select = (brand_id < 0 ? select : select.concat(" AND s.brand_id =:brand_id"));
        select = (fuel_type_id < 0 ? select : select.concat(" AND f.fuel_type_id =:fuel_type_id"));
        select = (city_id < 0 ? select : select.concat(" AND s.city_id =:city_id"));
        select = (rate < 0 ? select : select.concat(" AND r.rate >= :rate_bottom AND r.rate <= :rate_upper"));
        
        SQLQuery query = session.createSQLQuery(select);
        if(brand_id >= 0)
            query.setParameter("brand_id", brand_id);
        if(city_id >= 0)
            query.setParameter("city_id", city_id);
        if(fuel_type_id > 0)
            query.setParameter("fuel_type_id", fuel_type_id);
        if(rate >= 0){
            query.setParameter("rate_bottom", rate);
            query.setParameter("rate_upper", rate+1);
        }
        
        List<Offer> result = query
                .addEntity(Offer.class)
                .list();
        session.close();
        return result;
    }
}
