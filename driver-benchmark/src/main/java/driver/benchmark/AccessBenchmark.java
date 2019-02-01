package driver.benchmark;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.benchmark.driver.DataObjects;
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
public class AccessBenchmark {

    private ResultSet artistResultSet;

    @Setup(Level.Trial)
    public void setUp() throws SQLException {
        artistResultSet = DataObjects.getObjects().get("ARTIST");
        artistResultSet.next();
    }

    @Benchmark
    public Date getDate() throws SQLException {
        return artistResultSet.getDate(1);
    }

    @Benchmark
    public int getInt() throws SQLException {
        return artistResultSet.getInt(2);
    }

    @Benchmark
    public String getString() throws SQLException {
        return artistResultSet.getString(3);
    }
}
