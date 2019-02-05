import javax.persistence.EntityManager;

import hibernate.util.HibernateUtil;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import persistent.Artist;

import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        SessionFactory factory = HibernateUtil.getSessionFactory();

//        Session session = factory.openSession();
//        List<Artist> artists = session.createQuery("From Artist", Artist.class).getResultList();
//        try {
//            session.beginTransaction();
//            for(int i = 0; i < artists.size(); i++) {
//                artists.get(i).setName("test");
//                session.saveOrUpdate(artists.get(i));
//            }
//            session.getTransaction().commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Rollback in case of an error occurred.
//            session.getTransaction().rollback();
//        }
//        finally {
//            session.close();
//        }
        Session session = factory.openSession();
        List<Artist> artists = session.createQuery("From Artist", Artist.class).getResultList();
        session.beginTransaction();
        for(int i = 0; i < 1000; i++) {
            artists.get(i).setName("test - name" + i);
            artists.get(i).setDateOfBirth(new Date(i * 1000));
            session.merge(artists.get(i));
        }
        session.getTransaction().commit();

        session.close();
        System.out.println();
    }
}
