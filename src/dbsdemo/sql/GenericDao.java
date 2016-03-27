package dbsdemo.sql;

import java.util.List;
import javax.persistence.Table;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Baxos
 */
public class GenericDao<T> implements GenericDaoMethods<T> {
    
    protected SessionFactory sf;
    private final Class type;
    
    public GenericDao(Class<T> type){
        this.type = type;
    }

    @Override
    public void insert(T entity) {
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction t = session.beginTransaction();
        
        session.persist(entity);
        t.commit();
        session.close();
    }

    @Override
    public void insert(List<T> entityList) {
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction t = session.beginTransaction();
        
        int i = 0;
        for(T instance : entityList){
            session.persist(instance);
            if(i%50 == 0) {
                // Flush a batch of inserts and release memory
                session.flush();
                session.clear();
            }
            i++;
        }
        t.commit();
        session.close();
    }

    @Override
    public List<T> getAllAsObjects() {
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Table table = (Table) this.type.getAnnotation(Table.class);
        String select = "SELECT * FROM " + table.name();
        SQLQuery query = session.createSQLQuery(select);
        
        List<T> result = query
                .addEntity(this.type)
                .list();
        
        session.close();
        return result;
    }

    @Override
    public void eraseTable() {
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Table table = (Table) this.type.getAnnotation(Table.class);
        String sql = "DELETE FROM " + table.name();
        
        SQLQuery query = session.createSQLQuery(sql);        
        query.executeUpdate();
        
        session.close();
    }
    
}