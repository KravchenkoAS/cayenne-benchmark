package org.apache.cayenne.benchmark.server;

import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
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

    @Setup
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        objectContext = serverRuntime.newContext();
        dataObject = objectContext.newObject(Artist.class);
        dataObject.setId(1);
    }

    @Benchmark
    public ServerRuntime createRuntime() {
        ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        return serverRuntime;
    }

    @Benchmark
    public ObjectContext createContext() {
        ObjectContext objectContext = serverRuntime.newContext();
        return objectContext;
    }

    @Benchmark
    public Artist createObject() {
        Artist artist = objectContext.newObject(Artist.class);
        return artist;
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
