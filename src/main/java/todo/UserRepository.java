package todo;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserRepository {

    public UserAccount findById(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(UserAccount.class, id);
        }
    }

    /** Find by username or email (case-insensitive). */
    public UserAccount findByUsernameOrEmail(String value) {
        if (value == null) return null;

        String v = value.trim().toLowerCase();
        if (v.isEmpty()) return null;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<UserAccount> q = s.createQuery(
                    "from UserAccount u " +
                            "where lower(u.username) = :v or lower(u.email) = :v",
                    UserAccount.class
            );
            q.setParameter("v", v);
            return q.uniqueResult();
        }
    }
}

