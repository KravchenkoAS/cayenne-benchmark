package org.apache.cayenne.benchmark.server;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
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
import org.openjdk.jmh.annotations.Warmup;
import persistent.Artist;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
public class SelectQueryBenchmark {

    @State(Scope.Benchmark)
    public static class BaseSetup {

        ServerRuntime serverRuntime;
        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            objectContext = serverRuntime.newContext();
        }
    }

    @Benchmark
    public List<Artist> selectQueryBenchmark(BaseSetup baseSetup) {
        return SelectQuery.query(Artist.class)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectQueryWithExpBenchmark(BaseSetup baseSetup) {
        return SelectQuery.query(Artist.class, Artist.NAME.eq("a"))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectQueryDataRowBenchmark(BaseSetup baseSetup) {
        return SelectQuery.dataRowQuery(Artist.class)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectQueryDataRowExpBenchmark(BaseSetup baseSetup) {
        return SelectQuery.dataRowQuery(Artist.class, Artist.NAME.eq("a"))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public Artist selectQueryFirstBenchmark(BaseSetup baseSetup) {
        return SelectQuery.query(Artist.class)
                .selectFirst(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectQueryWithOrderingsBenchmark(BaseSetup baseSetup) {
        SelectQuery<Artist> query = SelectQuery.query(Artist.class);
        query.addOrdering(new Ordering(Artist.NAME.getName()));
        return query.select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectQueryDataRowWithOrderingsBenchmark(BaseSetup baseSetup) {
        SelectQuery<DataRow> query = SelectQuery.dataRowQuery(Artist.class);
        query.addOrdering(new Ordering(Artist.NAME.getName()));
        return query.select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Artist> selectQueryWithPrefetchBenchmark(BaseSetup baseSetup) {
        SelectQuery<Artist> query = SelectQuery.query(Artist.class);
        query.addPrefetch(Artist.PAINTINGS.disjoint());
        return query.select(baseSetup.objectContext);
    }

    @Benchmark
    public List<DataRow> selectQueryDataRowWithPrefetchBencmark(BaseSetup baseSetup) {
        SelectQuery<DataRow> query = SelectQuery.dataRowQuery(Artist.class);
        query.addPrefetch(Artist.PAINTINGS.disjoint());
        return query.select(baseSetup.objectContext);
    }
}
