package dao;

import javax.ejb.Remote;

import dao.generic.GenericDaoAPI;
import entities.User;

@Remote
public interface UserDaoAPI extends GenericDaoAPI<User> {
	public User getUser(String userName, String passwd);

	public void updateUser(User user);
}
