package org.apache.cayenne.benchmark.server;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
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
import persistent.Artist;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class ObjectSelectBenchmark {

    private static ServerRuntime serverRuntime;

    @Setup(Level.Iteration)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @State(Scope.Benchmark)
    public static class BaseSetup {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
        }
    }

    @State(Scope.Benchmark)
    public static class LocalCacheSetup {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            ObjectSelect.query(Artist.class)
                    .localCache()
                    .select(objectContext);
        }
    }

    @State(Scope.Benchmark)
    public static class SharedCacheSetup {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            ObjectSelect.query(Artist.class)
                    .sharedCache()
                    .select(objectContext);
        }
    }

    @State(Scope.Benchmark)
    public static class CacheGroupsSetup {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            ObjectSelect.query(Artist.class)
                    .cacheGroup("test-cache")
                    .select(objectContext);
        }
    }

    @Benchmark
    public List<Artist> selectAllBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public Artist selectFirstBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .selectFirst(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectAllWhereBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .where(Artist.NAME.eq("a"))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectWhereAndBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .where(Artist.NAME.eq("a"))
                .and(Artist.DATE_OF_BIRTH.gt(new Date(1000)))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectWhereOrBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .where(Artist.NAME.eq("a"))
                .or(Artist.DATE_OF_BIRTH.gt(new Date(1000)))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectOrderByAscBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .orderBy(Artist.DATE_OF_BIRTH.asc())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectOrderByDescBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .orderBy(Artist.DATE_OF_BIRTH.desc())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectOrderByDescByInsBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .orderBy(Artist.DATE_OF_BIRTH.descInsensitive())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectAllPrefetchDisjointBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .prefetch(Artist.PAINTINGS.disjoint())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectAllPrefetchDisjointByIdBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .prefetch(Artist.PAINTINGS.disjointById())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectAllPrefetchJointBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .prefetch(Artist.PAINTINGS.joint())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectLimitBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .limit(3)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectOffsetBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .offset(1)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectPageSizeBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .pageSize(1)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectStatementFetchSizeBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.query(Artist.class)
                .statementFetchSize(1)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectDataRowBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.dataRowQuery(Artist.class)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectDataRowWithExpBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.dataRowQuery(Artist.class, Artist.NAME.eq("a"))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectDbQueryBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.dbQuery("ARTIST")
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectDbQueryWithExpBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.dbQuery("ARTIST", Artist.NAME.eq("a"))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectLocalCacheBenchmark(LocalCacheSetup localCacheSetup) {
        return ObjectSelect.query(Artist.class)
                .localCache()
                .select(localCacheSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectSharedCacheBenchmark(SharedCacheSetup sharedCacheSetup) {
        return ObjectSelect.query(Artist.class)
                .sharedCache()
                .select(sharedCacheSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectCacheGroupBenchmark(CacheGroupsSetup cacheGroupsSetup) {
        return ObjectSelect.query(Artist.class)
                .cacheGroup("test-cache")
                .select(cacheGroupsSetup.objectContext);
    }

}
