package dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import dao.generic.GenericDao;
import entities.User;

@Stateless
public class UserDao extends GenericDao<User> implements UserDaoAPI {

	public UserDao() {
		this.type = User.class;
	}

	@Override
	public User getUser(String userName, String passwd) {

		String select = "SELECT * FROM users WHERE userName =:userName AND passwd =:passwd";
		Query query = em.createNativeQuery(select, User.class);

		List<User> result = query.setParameter("userName", userName)
				.setParameter("passwd", passwd).getResultList();

		return result.size() == 0 ? null : result.get(0);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void updateUser(User user) {
		try {
			String select = "UPDATE users SET name =:name, surname =:surname, userlevel =:userlevel WHERE id =:id";
			Query query = em.createNativeQuery(select);
	
			query.setParameter("name", user.getName()).setParameter("surname", user.getSurname())
					.setParameter("userlevel", user.getUserLevel()).setParameter("id", user.getId())
					.executeUpdate();
		}
		catch (Exception e) {
			Logger.getLogger(GenericDao.class.getName())
				.fatal("Failed to perform Hibernate update query", e);
		}
	}
}
