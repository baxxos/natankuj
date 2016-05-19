package dao.generic;

import java.util.List;

import javax.ejb.Remote;
// Generic database access object methods - used in every custom DAO
@Remote
public interface GenericDaoAPI<T> {
	public T insert(T entity);

	public void insert(List<T> entityList);

	public T getById(int id);

	public List<T> getAllAsObjects();

	public void deleteRecord(int id);

	public void eraseTable();
}
