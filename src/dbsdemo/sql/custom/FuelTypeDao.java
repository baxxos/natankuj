package dbsdemo.sql.custom;

import dbsdemo.entities.FuelType;
import dbsdemo.sql.GenericDao;

/**
 *
 * @author Baxos
 */
public class FuelTypeDao extends GenericDao<FuelType> {
    
    public FuelTypeDao(){
        super(FuelType.class);
    }
}
