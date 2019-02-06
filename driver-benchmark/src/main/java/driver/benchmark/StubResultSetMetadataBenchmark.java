package driver.benchmark;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.benchmark.driver.StubResultSetMetadata;
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

@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class StubResultSetMetadataBenchmark {

    Map<Integer, String> fields;

    @Setup(Level.Trial)
    public void setUp() {
        this.fields = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            fields.put(i, "test" + i);
        }
    }

    @Benchmark
    public ResultSetMetaData resultSetMetaDataCreation() throws SQLException {
        return new StubResultSetMetadata(fields);
    }
}
