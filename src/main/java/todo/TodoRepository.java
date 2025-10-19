package todo;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TodoRepository {

    /** Add and return the new id */
    public long add(String description) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = new TodoItem(description);
            s.persist(item);
            tx.commit();
            return item.getId();
        }
    }

    /** Read all — now sorted: incomplete first, then by id descending */
    public List<TodoItem> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "from TodoItem order by completed asc, id desc",
                    TodoItem.class
            ).getResultList();
        }
    }

    /** Read one */
    public TodoItem findById(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(TodoItem.class, id);
        }
    }

    /** Update description only */
    public void updateDescription(long id, String description) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = s.get(TodoItem.class, id);
            if (item != null) {
                item.setDescription(description);
                s.merge(item);
            }
            tx.commit();
        }
    }

    /** Delete */
    public void deleteById(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = s.get(TodoItem.class, id);
            if (item != null) s.remove(item);
            tx.commit();
        }
    }

    /** Toggle/Set completed */
    public void setCompleted(long id, boolean completed) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = s.get(TodoItem.class, id);
            if (item != null) {
                item.setCompleted(completed);
                s.merge(item);
            }
            tx.commit();
        }
    }
}
