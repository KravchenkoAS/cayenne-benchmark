package org.apache.cayenne.latest.benchmark.server;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
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

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class WriteComponentsBenchmark {

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
    public static class ContextSetup {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetup {

        ObjectContext objectContext;
        List<Artist> artists;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            artists = createArtists(objectContext, false);
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetupWithFields {

        ObjectContext objectContext;
        List<Artist> artists;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            artists = createArtists(objectContext, true);
        }
    }

    @Benchmark
    public ObjectContext contextCreation() {
        return serverRuntime.newContext();
    }

    @Benchmark
    public void objectsCreation(ContextSetup contextSetup) {
        for(int i = 0; i < objectsNumber; i++) {
            Artist artist = contextSetup.objectContext.newObject(Artist.class);
        }
    }

    @Benchmark
    public List<Artist> settingFields(ObjectSetup objectSetup) {
        objectSetup.artists.forEach(artist ->
                setArtistsFields(artist, "test", new Date(1000)));
        return objectSetup.artists;
    }

    @Benchmark
    public void contextCommit(ObjectSetupWithFields objectSetupWithFields) {
        objectSetupWithFields.objectContext.commitChanges();
    }

    @Benchmark
    public void totalExecution() {
        ObjectContext context = serverRuntime.newContext();
        for(int i = 0; i < objectsNumber; i++) {
            Artist artist = context.newObject(Artist.class);
            setArtistsFields(artist, "test", new Date(1000));
        }
        context.commitChanges();
    }

    private static List<Artist> createArtists(ObjectContext context, boolean setFields) {
        List<Artist> artists = new ArrayList<>();
        for(int i = 0; i < objectsNumber; i++) {
            Artist artist = context.newObject(Artist.class);
            if(setFields) {
                setArtistsFields(artist, "test" + i, new Date(i + 1000));
            }
            artists.add(artist);
        }
        return artists;
    }

    private static void setArtistsFields(Artist artist, String name, Date date) {
        artist.setId(1);
        artist.setName(name);
        artist.setDateOfBirth(date);
    }
}
