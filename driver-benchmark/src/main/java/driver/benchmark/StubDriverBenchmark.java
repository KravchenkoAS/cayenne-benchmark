package driver.benchmark;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.benchmark.driver.DataObjects;
import org.apache.cayenne.benchmark.driver.StubDriver;
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
public class StubDriverBenchmark {

    private StubDriver stubDriver;

    @Setup(Level.Trial)
    public void setUp() {
        stubDriver = new StubDriver();
    }

    @Benchmark
    public Connection connectDriver() throws SQLException {
        return stubDriver.connect("test-url", new Properties());
    }

    @Benchmark
    public Map<String, ResultSet> getObjects() {
        return DataObjects.getObjects();
    }
}
