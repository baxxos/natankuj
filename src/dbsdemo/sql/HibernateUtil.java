package dbsdemo.sql;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author Baxos
 */
public class HibernateUtil {
    
    private static final Logger LOG = Logger.getLogger(HibernateUtil.class.getName());
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static ServiceRegistry serviceRegistry;
    
    private static SessionFactory buildSessionFactory() {
        try {
            //Create the SessionFactory from hibernate.cfg.xml
            Configuration config = new Configuration();
            config.configure();
            
            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
            config.getProperties()).build();
            return new Configuration().configure().buildSessionFactory(serviceRegistry);
        }
        catch (Throwable ex) {
            //Log the exception
            LOG.log(Level.SEVERE, "Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void close(){
        sessionFactory.close();
        StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }
}
