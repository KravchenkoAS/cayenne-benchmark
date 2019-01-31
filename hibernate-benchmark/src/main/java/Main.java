import hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import persistent.Artist;

import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SessionFactory factory = HibernateUtil.getSessionFactory();

        Session session = factory.openSession();
        List<Artist> artists = session.createQuery("From Artist", Artist.class).getResultList();
        try {
            session.beginTransaction();
            for(int i = 0; i < artists.size(); i++) {
                artists.get(i).setName("test");
                session.saveOrUpdate(artists.get(i));
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            // Rollback in case of an error occurred.
            session.getTransaction().rollback();
        }
        finally {
            session.close();
        }
    }
}
