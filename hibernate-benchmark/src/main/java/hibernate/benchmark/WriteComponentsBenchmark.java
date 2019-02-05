package hibernate.benchmark;

import hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openjdk.jmh.annotations.*;
import persistent.Artist;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class WriteComponentsBenchmark {

    private static SessionFactory sessionFactory;

    @Setup(Level.Trial)
    public void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @State(Scope.Benchmark)
    public static class SessionSetup {
        Session session;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }

    }

    @State(Scope.Benchmark)
    public static class TransactionSetup {
        Session session;
        Transaction transaction;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Artist artist = new Artist();
            artist.setName("Test");
            artist.setDateOfBirth(new Date(1000));
            session.save(artist);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }

    }

    @State(Scope.Benchmark)
    public static class CreationSetup {

        Session session;
        Transaction transaction;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            for(int i = 0; i < 1000; i++) {
                Artist artist = new Artist();
                artist.setName("Test" + i);
                artist.setDateOfBirth(new Date(i * 1000 + 1));
                session.save(artist);
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            session.close();
        }
    }

    @Benchmark
    public Session openSession() {
        return sessionFactory.openSession();
    }

    @Benchmark
    public Transaction startTransaction(SessionSetup sessionSetup) {
        return sessionSetup.session.beginTransaction();
    }

    @Benchmark
    public Artist objectCreation(SessionSetup transactionSetup) {
        Artist artist = new Artist();
        artist.setName("Test");
        artist.setDateOfBirth(new Date(1000));
        transactionSetup.session.save(artist);
        return artist;
    }

    @Benchmark
    public void transactionCommit(TransactionSetup transactionSetup) {
        transactionSetup.transaction.commit();
    }

    @Benchmark
    public void fullExecution(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Artist artist = new Artist();
        artist.setName("Test");
        artist.setDateOfBirth(new Date(1000));
        session.save(artist);
        session.getTransaction().commit();
    }

    @Benchmark
    public void creationManyObjects() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        for(int i = 0; i < 1000; i++) {
            Artist artist = new Artist();
            artist.setName("Test" + i);
            artist.setDateOfBirth(new Date(i * 1000 + 1));
            session.save(artist);
        }
        transaction.commit();
    }
}
