package dbsdemo.sql.custom;

import dbsdemo.entities.User;
import dbsdemo.sql.GenericDao;
import dbsdemo.sql.HibernateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Baxos
 */
public class UserDao extends GenericDao<User> {
    
    public UserDao(){
        super(User.class);
    }
    
    public void updateUser(User user){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction t = session.beginTransaction();
        String select = "UPDATE users SET name =:name, surname =:surname, userlevel =:userlevel WHERE id =:id";
        SQLQuery query = session.createSQLQuery(select);
        
        query
                .setParameter("name", user.getName())
                .setParameter("surname", user.getSurname())
                .setParameter("userlevel", user.getUserLevel())
                .setParameter("id", user.getId())
                .executeUpdate();
        
        t.commit();
        session.close();
    }
       
    public User getUser(String userName, String passwd){
        
        sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        String select = "SELECT * FROM users WHERE userName =:userName AND passwd =:passwd";
        SQLQuery query = session.createSQLQuery(select);
        
        User result = (User) query
                .addEntity(User.class)
                .setParameter("userName", userName)
                .setParameter("passwd", passwd)
                .uniqueResult();
        
        session.close();
        return result;
    }
}
