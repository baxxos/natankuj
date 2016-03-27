package dbsdemo.sql.custom;

import dbsdemo.sql.GenericDao;
import dbsdemo.entities.Station;
import org.hibernate.SessionFactory;

/**
 *
 * @author Baxos
 */
public class StationDao extends GenericDao<Station> {

    public StationDao(){
        super(Station.class);
    }
}
