package hibernate.benchmark;

import hibernate.util.HibernateUtil;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.Query;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import persistent.Artist;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class ReadingBenchmark {

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
        public void tearDown(){
            session.close();
        }
    }

    @State(Scope.Benchmark)
    public static class QuerySetup {
        Session session;
        Query<Artist> query;

        @Setup(Level.Invocation)
        public void setUp() {
            session = sessionFactory.openSession();
            query = session.createQuery("From Artist", Artist.class);
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
    public Query<Artist> createQuery(SessionSetup sessionSetup) {
        return sessionSetup.session.createQuery("From Artist", Artist.class);
    }

    @Benchmark
    public ScrollableResults getScrollableResults(QuerySetup querySetup) {
        return querySetup.query.scroll();
    }

    @Benchmark
    public Stream<Artist> getStream(QuerySetup querySetup) {
        return querySetup.query.stream();
    }

    @Benchmark
    public List<Artist> getResultList(QuerySetup querySetup) {
        return querySetup.query.getResultList();
    }

    @Benchmark
    public List<Artist> queryExecution(SessionSetup sessionSetup) {
        Query<Artist> query = sessionSetup.session
                .createQuery("From Artist", Artist.class);
        return query.getResultList();
    }

    @Benchmark
    public void closeSession(SessionSetup sessionSetup, Blackhole blackhole) {
        sessionSetup.session.close();
        blackhole.consume(sessionSetup.session);
    }

    @Benchmark
    public List<Artist> fullExecution() {
        Session session = sessionFactory.openSession();
        Query<Artist> query = session
                .createQuery("From Artist", Artist.class);
        List<Artist> artists = query.getResultList();
        session.close();
        return artists;
    }

    @Benchmark
    public Stream<Artist> fullStreamExecution() {
        Session session = sessionFactory.openSession();
        Query<Artist> query = session
                .createQuery("From Artist", Artist.class);
        Stream<Artist> stream = query.stream();
        session.close();
        return stream;
    }

    @Benchmark
    public List<Artist> fullReadOnlyExecution() {
        Session session = sessionFactory.openSession();
        Query<Artist> query = session
                .createQuery("From Artist", Artist.class)
                .setHint(QueryHints.HINT_READONLY, true);
        List<Artist> artists = query.getResultList();
        session.close();
        return artists;
    }
}
