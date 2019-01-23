package org.apache.cayenne.benchmark.server;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.annotations.Warmup;
import persistent.Artist;
import persistent.Painting;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
public class ColumnSelectBenchmark {

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
    public List<Object[]> columnQueryBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<LocalDate> columnQueryObjBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryCountBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH)
                .count()
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryCountPaintingsBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME, Artist.PAINTINGS.count())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryMaxBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .max(Artist.ID)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryMinBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .min(Artist.ID)
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryAvgBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .avg(Artist.PAINTINGS.count())
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQuerySumBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .sum(Artist.PAINTINGS.dot(Painting.ID))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryHavingBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .having(Artist.NAME.eq("a"))
                .select(baseSetup.objectContext);
    }

    @Benchmark
    public List<Object[]> columnQueryDistinctBenchmark(BaseSetup baseSetup) {
        return ObjectSelect.columnQuery(Artist.class, Artist.DATE_OF_BIRTH, Artist.ID, Artist.NAME)
                .distinct()
                .select(baseSetup.objectContext);
    }
}
