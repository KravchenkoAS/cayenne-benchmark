package driver.benchmark;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.benchmark.driver.StubPreparedStatement;
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
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class StubPreparedStatementBenchmark {

    private StubPreparedStatement stubPreparedStatement;

    @Setup(Level.Trial)
    public void setUp() {
        stubPreparedStatement = new StubPreparedStatement("SELECT DATE_OF_BIRTH AS DATE_1, NAME AS NAME_1, ID AS ID_1 FROM ARTIST AS ARTIST_1");
    }

    @Benchmark
    public StubPreparedStatement createStatement() {
        return new StubPreparedStatement("SELECT DATE_OF_BIRTH AS DATE_1, NAME AS NAME_1, ID AS ID_1 FROM ARTIST AS ARTIST_1");
    }

    @Benchmark
    public ResultSet executeStatementBenchmark() throws SQLException {
        return stubPreparedStatement.executeQuery();
    }
}
