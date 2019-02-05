package org.apache.cayenne.benchmark.server;

import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
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

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 4, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class ServerBenchmark {

    private ServerRuntime serverRuntime;
    private ObjectContext objectContext;
    private Artist dataObject;

    @Setup(Level.Iteration)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        objectContext = serverRuntime.newContext();
        dataObject = objectContext.newObject(Artist.class);
        dataObject.setId(1);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @Benchmark
    public ServerRuntime createRuntime() {
        return ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
    }

    @Benchmark
    public ObjectContext createContext() {
        return serverRuntime.newContext();
    }

    @Benchmark
    public Artist createObject() {
        return objectContext.newObject(Artist.class);
    }

    @Benchmark
    public Artist updateObject() {
        dataObject.setName("Name");
        return dataObject;
    }

    @Benchmark
    public void commitChanges() {
        objectContext.commitChanges();
    }
}
