package dbsdemo.sql.custom;

import dbsdemo.entities.Offer;
import dbsdemo.sql.GenericDao;

/**
 *
 * @author Baxos
 */
public class OfferDao extends GenericDao<Offer> {

    public OfferDao(){
        super(Offer.class);
    }
}
