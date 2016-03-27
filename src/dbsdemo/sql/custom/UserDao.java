package dbsdemo.sql.custom;

import dbsdemo.entities.User;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author Baxos
 */
public class UserDao extends GenericDao<User> {
    
    public UserDao(){
        super(User.class);
    }
       
    public List<User> getUser(String userName, String passwd){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT * FROM users WHERE userName =:userName AND passwd =:passwd";
        SQLQuery query = session.createSQLQuery(select);
        
        List<User> result = query
                .addEntity(User.class)
                .setParameter("userName", userName)
                .setParameter("passwd", passwd)
                .list();

        session.close();
        return result;
    }
}
