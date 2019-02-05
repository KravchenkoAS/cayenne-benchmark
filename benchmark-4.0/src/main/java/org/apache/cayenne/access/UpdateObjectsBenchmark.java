package org.apache.cayenne.access;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.util.FakeDataChannel;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import persistent.Artist;

@Warmup(iterations = 6, time = 3)
@Measurement(iterations = 10, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class UpdateObjectsBenchmark {

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

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @State(Scope.Benchmark)
    public static class UpdateSetup {
        ObjectContext context;
        List<Artist> artists;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
        }
    }

    @State(Scope.Benchmark)
    public static class FakeDataChannelSetup {

        ObjectContext context;
        List<Artist> artists;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
            DataChannel dataChannel = new FakeDataChannel();
            ((DataContext) context).setChannel(dataChannel);
        }
    }

    @State(Scope.Benchmark)
    public static class OnSyncSetup {

        ObjectContext context;
        List<Artist> artists;
        DataChannel dataChannel;
        ObjectStore objectStore;
        GraphDiff graphDiff;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
            updateArtists(artists);
            objectStore = ((DataContext) context).getObjectStore();
            graphDiff = new ObjectStoreGraphDiff(objectStore);
            dataChannel = context.getChannel();
        }
    }

    @State(Scope.Benchmark)
    public static class ValidationSetup {

        ObjectContext context;
        List<Artist> artists;
        ObjectStore objectStore;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
            updateArtists(artists);
            objectStore = ((DataContext) context).getObjectStore();
        }
    }

    @State(Scope.Benchmark)
    public static class FlushSetup {
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
            updateArtists(artists);
            objectStore = ((DataContext) context).getObjectStore();
            graphDiff = new ObjectStoreGraphDiff(objectStore);
            dataDomain = (DataDomain)context.getChannel();
        }
    }

    @Benchmark
    public void updateArtist(UpdateSetup updateSetup) {
        updateArtists(updateSetup.artists);
        updateSetup.context.commitChanges();
    }

    @Benchmark
    public void updateArtistsWithFakeDataChannel(FakeDataChannelSetup fakeDataChannelSetup) {
        updateArtists(fakeDataChannelSetup.artists);
        fakeDataChannelSetup.context.commitChanges();
    }

    @Benchmark
    public void flushBenchmark(FlushSetup flushSetup) {
        new DataDomainFlushAction(flushSetup.dataDomain).flush((DataContext) flushSetup.context, flushSetup.graphDiff);
    }

    @Benchmark
    public boolean validationBenchmark(ValidationSetup validationSetup) {
        ObjectStoreGraphDiff changes = validationSetup.objectStore.getChanges();
        return  ((DataContext) validationSetup.context).isValidatingObjectsOnCommit() ? changes.validateAndCheckNoop() : changes.isNoop();
    }

    @Benchmark
    public void onSyncBenchmark(OnSyncSetup onSyncSetup) {
        onSyncSetup.dataChannel.onSync(onSyncSetup.context, onSyncSetup.graphDiff, 2);
    }


    private static void updateArtists(List<Artist> artists) {
        if(artists.size() < objectsNumber) {
            throw new RuntimeException("ObjectsNumber can't be more than collection's size");
        }
        for(int i = 0; i < objectsNumber; i++) {
            Artist artist = artists.get(i);
            artist.setName("Test" + i);
            artist.setDateOfBirth(new Date(i + objectsNumber));
        }
    }
}
