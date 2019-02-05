package hibernate.benchmark;

import hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openjdk.jmh.annotations.*;
import persistent.Artist;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 6, time = 2)
@Measurement(iterations = 8, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class UpdateBenchmark {
    private static SessionFactory sessionFactory;

    @Setup(Level.Trial)
    public void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @State(Scope.Benchmark)
    public static class ObjectsSetup {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Setup1Field1Object {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
            updateObj(session, artistList, 1, false);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Setup2Field1Object {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
            updateObj(session, artistList, 1, true);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Setup1Field100Object {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
            updateObj(session, artistList, 100, false);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Setup2Field100Object {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
            updateObj(session, artistList, 100, true);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Setup1Field1000Object {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
            updateObj(session, artistList, 1000, false);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Setup2Field1000Object {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
            transaction = session.beginTransaction();
            updateObj(session, artistList, 1000, true);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class Update1000Objects {
        Session session;
        Transaction transaction;
        List<Artist> artistList;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            artistList = session.createQuery("From Artist", Artist.class)
                    .getResultList();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @Benchmark
    public void updateOneObjOneField(ObjectsSetup objectsSetup) {
       updateObj(objectsSetup.session, objectsSetup.artistList, 1, false);
    }

    @Benchmark
    public void updateOneObjTwoField(ObjectsSetup objectsSetup) {
        updateObj(objectsSetup.session, objectsSetup.artistList, 1, true);
    }

    @Benchmark
    public void updateHundredObjOneField(ObjectsSetup objectsSetup) {
        updateObj(objectsSetup.session, objectsSetup.artistList, 100, false);
    }

    @Benchmark
    public void updateHundredObjTwoField(ObjectsSetup objectsSetup) {
        updateObj(objectsSetup.session, objectsSetup.artistList, 100, true);
    }

    @Benchmark
    public void updateThousandObjOneField(ObjectsSetup objectsSetup) {
        updateObj(objectsSetup.session, objectsSetup.artistList, 1000, false);
    }

    @Benchmark
    public void updateThousandObjTwoField(ObjectsSetup objectsSetup) {
        updateObj(objectsSetup.session, objectsSetup.artistList, 1000, true);
    }

    @Benchmark
    public void commit1F1Obj(Setup1Field1Object setup1Field1Object) {
        setup1Field1Object.session.getTransaction().commit();
    }

    @Benchmark
    public void commit2F1Obj(Setup2Field1Object setup2Field1Object) {
        setup2Field1Object.session.getTransaction().commit();
    }

    @Benchmark
    public void commit1F100Obj(Setup1Field100Object setup1Field100Object) {
        setup1Field100Object.session.getTransaction().commit();
    }

    @Benchmark
    public void commit2F100Obj(Setup2Field100Object setup2Field100Object) {
        setup2Field100Object.session.getTransaction().commit();
    }

    @Benchmark
    public void commit1F1000Obj(Setup1Field1000Object setup1Field1000Object) {
        setup1Field1000Object.session.getTransaction().commit();
    }

    @Benchmark
    public void commit2F1000Obj(Setup2Field1000Object setup2Field1000Object) {
        setup2Field1000Object.session.getTransaction().commit();
    }

    @Benchmark
    public void update1000Obj(Update1000Objects update1000Objects) {
        Transaction transaction = update1000Objects.session.beginTransaction();
        updateObj(update1000Objects.session, update1000Objects.artistList, 1000, true);
        transaction.commit();
    }

    private static void updateObj(Session session, List<Artist> artists, int num, boolean updateDate) {
        for(int i = 0; i < num; i++) {
            Artist artist = artists.get(i);
            artist.setName("Test" + i);
            if(updateDate) {
                artist.setDateOfBirth(new Date(i * 1000 + 1));
            }
            session.merge(artist);
        }
    }
}
