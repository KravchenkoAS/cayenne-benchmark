package org.apache.cayenne.access;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.event.EventManager;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.query.ObjectSelect;
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
import org.openjdk.jmh.annotations.Warmup;
import persistent.Artist;

@Warmup(iterations = 6, time = 3)
@Measurement(iterations = 10, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class DataDomainFlushActionBenchmark {

    private static final int DEFAULT_OBJECTS_NUMBER = 1;
    private static int objectsNumber;
    static {
        String numberParam = System.getProperty("objectsNumber");
        objectsNumber = numberParam != null ? Integer.valueOf(numberParam) : DEFAULT_OBJECTS_NUMBER;
    }

    private static ServerRuntime serverRuntime;

    @Setup(Level.Iteration)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> ServerModule.contributeProperties(binder)
                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(false)))
                .addModule(binder -> binder.bind(EventManager.class).toInstance(new NoopEventManager()))
                .build();
        ObjectContext context = serverRuntime.newContext();
    }

    @State(Scope.Benchmark)
    public static class InsertSetup {
        ObjectContext context;
        GraphDiff graphDiff;
        ObjectStore objectStore;
        DataDomain dataDomain;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            for(int i = 0; i < objectsNumber; i++) {
                Artist artist = context.newObject(Artist.class);
                artist.setId(i);
                artist.setName("Test" + i);
                artist.setDateOfBirth(new Date(i + objectsNumber));
            }
            objectStore = ((DataContext) context).getObjectStore();
            graphDiff = new ObjectStoreGraphDiff(objectStore);
            dataDomain = (DataDomain)context.getChannel();
        }
    }

    @State(Scope.Benchmark)
    public static class UpdateSetup {
        ObjectContext context;
        List<Artist> artists;
        GraphDiff graphDiff;
        ObjectStore objectStore;
        DataDomain dataDomain;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
            if(artists.size() < objectsNumber) {
                throw new RuntimeException("ObjectsNumber can't be more than collection's size");
            }
            for(int i = 0; i < objectsNumber; i++) {
                Artist artist = artists.get(i);
                artist.setName("test" + i);
                artist.setDateOfBirth(new Date(i + objectsNumber));
            }
            objectStore = ((DataContext) context).getObjectStore();
            graphDiff = new ObjectStoreGraphDiff(objectStore);
            dataDomain = (DataDomain)context.getChannel();
        }
    }

    @State(Scope.Benchmark)
    public static class DeleteSetup {
        ObjectContext context;
        List<Artist> artists;
        GraphDiff graphDiff;
        ObjectStore objectStore;
        DataDomain dataDomain;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
            if(artists.size() < objectsNumber) {
                throw new RuntimeException("ObjectsNumber can't be more than collection's size");
            }
            for(int i = 0; i < objectsNumber; i++) {
                Artist artist = artists.get(i);
                context.deleteObject(artist);
            }
            objectStore = ((DataContext) context).getObjectStore();
            graphDiff = new ObjectStoreGraphDiff(objectStore);
            dataDomain = (DataDomain)context.getChannel();
        }
    }

    @State(Scope.Benchmark)
    public static class DeleteSetupPrefetchPaintings {
        ObjectContext context;
        List<Artist> artists;
        GraphDiff graphDiff;
        ObjectStore objectStore;
        DataDomain dataDomain;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .prefetch(Artist.PAINTINGS.disjoint())
                    .select(context);
            if(artists.size() < objectsNumber) {
                throw new RuntimeException("ObjectsNumber can't be more than collection's size");
            }
            for(int i = 0; i < objectsNumber; i++) {
                Artist artist = artists.get(i);
                context.deleteObject(artist);
            }
            objectStore = ((DataContext) context).getObjectStore();
            graphDiff = new ObjectStoreGraphDiff(objectStore);
            dataDomain = (DataDomain)context.getChannel();
        }
    }

    @Benchmark
    public void flushInsert(InsertSetup insertSetup) {
        new DataDomainFlushAction(insertSetup.dataDomain).flush((DataContext) insertSetup.context, insertSetup.graphDiff);
    }

    @Benchmark
    public void flushUpdate(UpdateSetup updateSetup) {
        new DataDomainFlushAction(updateSetup.dataDomain).flush((DataContext) updateSetup.context, updateSetup.graphDiff);
    }

    @Benchmark
    public void flushDelete(DeleteSetup deleteSetup) {
        new DataDomainFlushAction(deleteSetup.dataDomain).flush((DataContext) deleteSetup.context, deleteSetup.graphDiff);
    }

    @Benchmark
    public void flushDeleteWithPrefetch(DeleteSetupPrefetchPaintings deleteSetupPrefetchPaintings) {
        new DataDomainFlushAction(deleteSetupPrefetchPaintings.dataDomain)
                .flush((DataContext) deleteSetupPrefetchPaintings.context, deleteSetupPrefetchPaintings.graphDiff);
    }
}
