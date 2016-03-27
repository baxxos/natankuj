package dbsdemo.sql;

import java.util.List;

/**
 *
 * @author Baxos
 */
public interface GenericDaoMethods<T> {
    public void insert(T entity);
    public void insert(List<T> entityList);
    public List<T> getAllAsObjects();
    public void eraseTable();
}
