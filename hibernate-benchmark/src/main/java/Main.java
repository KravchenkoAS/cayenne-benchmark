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

        Stream res = session.createQuery("From Artist").stream();
        res.forEach(r -> System.out.println(((Artist)r).getName()));
        session.close();
        System.out.println();
    }
}
