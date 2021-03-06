package org.apache.cayenne.stable.benchmark.server;

import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.event.EventManager;
import org.apache.cayenne.stable.benchmark.event.utils.NoopEventManager;
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

    @Setup(Level.Trial)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> ServerModule.contributeProperties(binder)
                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(false)))
                .addModule(binder -> binder.bind(EventManager.class).toInstance(new NoopEventManager()))
                .build();
        objectContext = serverRuntime.newContext();
        dataObject = objectContext.newObject(Artist.class);
        dataObject.setId(1);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @Benchmark
    public ServerRuntime createRuntime() {
        return ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> ServerModule.contributeProperties(binder)
                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(false)))
                .addModule(binder -> binder.bind(EventManager.class).toInstance(new NoopEventManager()))
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
