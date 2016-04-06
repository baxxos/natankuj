package dbsdemo.sql.custom;

import dbsdemo.entities.FuelBrand;
import dbsdemo.sql.GenericDao;

/**
 *
 * @author Baxos
 */
public class FuelBrandDao extends GenericDao<FuelBrand> {
    
    public FuelBrandDao(){
        super(FuelBrand.class);
    }
}
