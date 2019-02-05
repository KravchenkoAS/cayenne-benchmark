package org.apache.cayenne.latest.benchmark.server;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
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

@Warmup(iterations = 6, time = 2)
@Measurement(iterations = 8, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class UpdateComponentsBenchmark {

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
                .build();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @State(Scope.Benchmark)
    public static class ObjectsSetup {

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
    public static class SetupOneField {
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateOneField(context, objectsNumber);
        }
    }

    @State(Scope.Benchmark)
    public static class SetupTwoField {
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateTwoFields(context, objectsNumber);
        }
    }

    @Benchmark
    public List<Artist> updateOneField(ObjectsSetup objectsSetup) {
        return updateArtists(objectsSetup.artists, objectsNumber, false);
    }

    @Benchmark
    public List<Artist> updateTwoFields(ObjectsSetup objectsSetup) {
        return updateArtists(objectsSetup.artists, objectsNumber, true);
    }

    @Benchmark
    public void commitOneField(SetupOneField setupOneField) {
        setupOneField.context.commitChanges();
    }

    @Benchmark
    public void commitTwoFields(SetupTwoField setupTwoField) {
        setupTwoField.context.commitChanges();
    }

    private static List<Artist> updateArtists(List<Artist> artists, int size, boolean updateDate) {
        if(artists.size() < size) {
            throw new RuntimeException("ObjectsNumber can't be more than collection's size");
        }
        for(int i = 0; i < size; i++) {
            Artist artist = artists.get(i);
            artist.setName("UpdateName" + i);
            if(updateDate) {
                artist.setDateOfBirth(new Date(1000));
            }
        }
        return artists;
    }

    private static List<Artist> getArtistsForUpdateOneField(ObjectContext context, int size) {
        return updateArtists(ObjectSelect.query(Artist.class).select(context), size, false);
    }

    private static List<Artist> getArtistsForUpdateTwoFields(ObjectContext context, int size) {
        return updateArtists(ObjectSelect.query(Artist.class).select(context), size, true);
    }
}
