package hibernate.benchmark;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import hibernate.util.HibernateUtil;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import persistent.Artist;

@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class ReadingEntityManagerBenchmark {

    private static SessionFactory sessionFactory;

    @Setup(Level.Trial)
    public void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @State(Scope.Benchmark)
    public static class ManagerSetup {
        EntityManager entityManager;

        @Setup(Level.Invocation)
        public void setUp() {
            entityManager = sessionFactory.createEntityManager();
        }

        @TearDown(Level.Invocation)
        public void tearDown(){
            entityManager.close();
        }
    }

    @State(Scope.Benchmark)
    public static class QuerySetup {
        EntityManager entityManager;
        TypedQuery<Artist> query;

        @Setup(Level.Invocation)
        public void setUp() {
            entityManager = sessionFactory.createEntityManager();
            query = entityManager.createQuery("From Artist", Artist.class);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            entityManager.close();
        }
    }

    @Benchmark
    public EntityManager createEntityManager() {
        return sessionFactory.createEntityManager();
    }

    @Benchmark
    public TypedQuery<Artist> createQuery(ManagerSetup managerSetup) {
        return managerSetup.entityManager.createQuery("From Artist", Artist.class);
    }

    @Benchmark
    public List<Artist> getResultList(QuerySetup querySetup) {
        return querySetup.query.getResultList();
    }

    @Benchmark
    public List<Artist> queryExecution(ManagerSetup managerSetup) {
        TypedQuery<Artist> query = managerSetup.entityManager
                .createQuery("From Artist", Artist.class);
        return query.getResultList();
    }

    @Benchmark
    public void closeManager(ManagerSetup managerSetup, Blackhole blackhole) {
        managerSetup.entityManager.close();
        blackhole.consume(managerSetup.entityManager);
    }

    @Benchmark
    public List<Artist> fullExecution() {
        EntityManager entityManager = sessionFactory.createEntityManager();
        TypedQuery<Artist> query = entityManager
                .createQuery("From Artist", Artist.class);
        List<Artist> artists = query.getResultList();
        entityManager.close();
        return artists;
    }

}
