package dao.generic;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import entities.Offer;
// Implementation of generic dao (CRUD like) methods - insert, delete, get by index
@Stateless
public class GenericDao<T> implements GenericDaoAPI<T> {
    
	@PersistenceContext
	protected EntityManager em;
	protected Class<T> type;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T insert(T entity) {
    	try {
    		em.persist(entity);
    	}
    	catch (Exception e) {
    		Logger.getLogger(GenericDao.class.getName())
    			.fatal("Failed to perform Hibernate insert query", e);
    	}
    	return entity;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void insert(List<T> entityList) {
        try {
	        int i = 0;
	        for(T instance : entityList){
	            if(instance instanceof Offer)
	                em.merge(instance);
	            else
	            	em.persist(instance);
	            if(i%50 == 0) {
	                // Flush a batch of inserts and release memory
	            	em.flush();
	            	em.clear();
	            }
	            i++;
	        }
        } catch (Exception e) {
        	Logger.getLogger(GenericDao.class.getName())
    			.fatal("Failed to perform Hibernate batch insert query", e);
        }
    }
    
    @Override
    public T getById(int id){
        Table table = (Table) this.type.getAnnotation(Table.class);
        String select = "SELECT * FROM " + table.name() + " WHERE id =:id";
        
        List<T> result = em.createNativeQuery(select, this.type)
        		.setParameter("id", id).getResultList();
        
        if(result.size() == 0){
        	Logger.getLogger(GenericDao.class.getName())
        		.warn("GenericDao returning NULL instance for class: "+this.type+" and id: "+id);
        }
        
        for(T instance : result){
        	em.detach(instance);
        }
        
        return result.size() == 0 ? null : result.get(0);
    }

    @Override
    public List<T> getAllAsObjects() {
    	List<T> result = new ArrayList<>();
    	try {
	        Table table = (Table) this.type.getAnnotation(Table.class);
	        String select = "SELECT * FROM " + table.name();
	        result = em.createNativeQuery(select, this.type).getResultList();
	        
	        for (T listNode : result){
	        	em.detach(listNode);
	        }
    	}
    	catch (Exception e){
    		Logger.getLogger(GenericDao.class.getName())
    			.fatal("Failed to perform Hibernate batch get query", e);
    	}
        return result;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteRecord(int id){
    	try {
	        Table table = (Table) this.type.getAnnotation(Table.class);
	        String sql = "DELETE FROM " + table.name() + " WHERE id =:id";
	              
	        em.createNativeQuery(sql).setParameter("id", id).executeUpdate();
    	}
    	catch (Exception e){
    		Logger.getLogger(GenericDao.class.getName())
    			.fatal("Failed to perform Hibernate delete query (id: "+id+")");
    	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eraseTable() {
        Table table = (Table) this.type.getAnnotation(Table.class);
        try {
        String sql = "DELETE FROM " + table.name();
        em.createNativeQuery(sql).executeUpdate(); 
        }
        catch (Exception e){
        	Logger.getLogger(GenericDao.class.getName())
    			.fatal("Failed to execute Hibernate erase table query (table: "+table+")");
        }
    }

	public void setType(Class<T> type) {
		this.type = type;
	}
}
