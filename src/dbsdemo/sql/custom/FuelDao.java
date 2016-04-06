
package dbsdemo.sql.custom;

import dbsdemo.entities.Fuel;
import dbsdemo.sql.GenericDao;

/**
 *
 * @author Baxos
 */
public class FuelDao extends GenericDao<Fuel> {
    
    public FuelDao(){
        super(Fuel.class);
    }
}
