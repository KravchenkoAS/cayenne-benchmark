package org.apache.cayenne.stable.benchmark.server;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.event.EventManager;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.reflect.ClassDescriptor;
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
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import persistent.Painting;

@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@Threads(2)
@State(Scope.Benchmark)
public class ObjectResolverBenchmark {

    private ServerRuntime serverRuntime;
    private ObjectContext objectContext;
    private List<DataRow> dataRowList;
    private ClassDescriptor descriptor;

    @Setup(Level.Trial)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> ServerModule.contributeProperties(binder)
                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(false)))
                .addModule(binder -> binder.bind(EventManager.class).toInstance(new NoopEventManager()))
                .build();
        objectContext = serverRuntime.newContext();
        dataRowList = ObjectSelect.dataRowQuery(Painting.class)
                .select(objectContext);
        descriptor = objectContext.getEntityResolver().getClassDescriptor("Painting");
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @Benchmark
    public List convertToObj(){
        return  ((DataContext) objectContext).objectsFromDataRows(descriptor, dataRowList);
    }
}
