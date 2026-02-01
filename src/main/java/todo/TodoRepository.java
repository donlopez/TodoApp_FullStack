package todo;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class TodoRepository {

    /** Add and return the new id for a given user (default: not completed, MEDIUM priority, no due date). */
    public long add(String description, long userId) {
        return add(description, userId, false, TodoItem.Priority.MEDIUM, null);
    }

    /**
     * Add and return the new id for a given user, with completed + priority.
     * (Old signature kept for compatibility – delegates to the new method with no due date.)
     */
    public long add(String description,
                    long userId,
                    boolean completed,
                    TodoItem.Priority priority) {
        return add(description, userId, completed, priority, null);
    }

    /** New: add with completed + priority + dueDate. */
    public long add(String description,
                    long userId,
                    boolean completed,
                    TodoItem.Priority priority,
                    LocalDateTime dueDate) {

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();

            UserAccount user = s.get(UserAccount.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + userId);
            }

            TodoItem item = new TodoItem(description, user);
            item.setCompleted(completed);
            item.setPriority(priority);
            item.setDueDate(dueDate);

            s.persist(item);

            tx.commit();
            return item.getId();
        }
    }

    /**
     * All todos for a specific user:
     *  - not completed first
     *  - then completed
     *  - newest first within each group
     */
    public List<TodoItem> findAllByUser(long userId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<TodoItem> q = s.createQuery(
                    "from TodoItem t " +
                            "where t.user.id = :uid " +
                            "order by t.completed asc, t.createdAt desc",
                    TodoItem.class
            );
            q.setParameter("uid", userId);
            return q.getResultList();
        }
    }

    public TodoItem findById(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(TodoItem.class, id);
        }
    }

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

    /**
     * Old update method (kept for compatibility):
     * updates description, completed flag, and priority – leaves dueDate unchanged.
     */
    public void updateTask(long id,
                           String description,
                           boolean completed,
                           TodoItem.Priority priority) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = s.get(TodoItem.class, id);
            if (item != null) {
                item.setDescription(description);
                item.setCompleted(completed);
                item.setPriority(priority);
                s.merge(item);
            }
            tx.commit();
        }
    }

    /** New update: also sets dueDate. */
    public void updateTask(long id,
                           String description,
                           boolean completed,
                           TodoItem.Priority priority,
                           LocalDateTime dueDate) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = s.get(TodoItem.class, id);
            if (item != null) {
                item.setDescription(description);
                item.setCompleted(completed);
                item.setPriority(priority);
                item.setDueDate(dueDate);
                s.merge(item);
            }
            tx.commit();
        }
    }

    public void deleteById(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            TodoItem item = s.get(TodoItem.class, id);
            if (item != null) {
                s.remove(item);
            }
            tx.commit();
        }
    }

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
