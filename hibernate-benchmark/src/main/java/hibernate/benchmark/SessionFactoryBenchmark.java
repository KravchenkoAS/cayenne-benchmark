package hibernate.benchmark;

import hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 4, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SessionFactoryBenchmark {

    private SessionFactory sessionFactory;
    private Session session;

    @Setup(Level.Iteration)
    public void setUp() {
        sessionFactory = HibernateUtil.getSessionFactory();
        session = sessionFactory.openSession();
    }

    @Benchmark
    public SessionFactory buildSessionFactory() {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()//
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    @Benchmark
    public Session openSession() {
        return sessionFactory.openSession();
    }

    @Benchmark
    public void closeSession(Blackhole blackhole) {
        session.close();
        blackhole.consume(session);
    }
}
