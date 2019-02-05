package org.apache.cayenne.latest.benchmark.server;

import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataRowStore;
import org.apache.cayenne.access.DataRowStoreFactory;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.cache.NestedQueryCache;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.ObjectStoreFactory;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.tx.TransactionFactory;
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

@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class ContextBenchmark {

    private ServerRuntime serverRuntime;
    private DataDomain dataDomain;
    private DataRowStoreFactory dataRowStoreFactory;
    private ObjectStoreFactory objectStoreFactory;
    private QueryCache queryCache;
    private TransactionFactory transactionFactory;
    private DataRowStore snapshotCache;

    @Setup(Level.Iteration)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        dataDomain = serverRuntime.getDataDomain();
        dataRowStoreFactory = serverRuntime.getInjector().getInstance(DataRowStoreFactory.class);
        objectStoreFactory = serverRuntime.getInjector().getInstance(ObjectStoreFactory.class);
        queryCache = serverRuntime.getInjector().getInstance(QueryCache.class);
        transactionFactory = serverRuntime.getInjector().getInstance(TransactionFactory.class);
        snapshotCache = (dataDomain.isSharedCacheEnabled())
                ? dataDomain.getSharedSnapshotCache()
                : dataRowStoreFactory.createDataRowStore(dataDomain.getName());
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @Benchmark
    public ObjectContext contextCreation() {
        return serverRuntime.newContext();
    }

    @Benchmark
    public DataRowStore createDataRowStore() {
        return (dataDomain.isSharedCacheEnabled())
                ? dataDomain.getSharedSnapshotCache()
                : dataRowStoreFactory.createDataRowStore(dataDomain.getName());
    }

    @Benchmark
    public DataContext createDataContext() {
        return new DataContext(
                dataDomain, objectStoreFactory.createObjectStore(snapshotCache));
    }

    @Benchmark
    public ObjectStore createObjectStore() {
        return objectStoreFactory.createObjectStore(snapshotCache);
    }

    @Benchmark
    public ObjectContext createContextFromFactory() {
        DataContext context = new DataContext(
                dataDomain, objectStoreFactory.createObjectStore(snapshotCache));
        context.setValidatingObjectsOnCommit(dataDomain.isValidatingObjectsOnCommit());
        context.setQueryCache(new NestedQueryCache(queryCache));
        context.setTransactionFactory(transactionFactory);
        return context;
    }
}
