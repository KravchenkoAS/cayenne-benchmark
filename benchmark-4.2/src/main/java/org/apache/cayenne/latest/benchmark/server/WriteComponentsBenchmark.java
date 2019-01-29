package org.apache.cayenne.latest.benchmark.server;

import java.sql.Date;
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
        Artist artist;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            artist = objectContext.newObject(Artist.class);
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetupWithFields {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            Artist artist = objectContext.newObject(Artist.class);
            setArtistsFields(artist, "Test", new Date(1000));
        }
    }

    @State(Scope.Benchmark)
    public static class SetupWithManyObjects {

        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            objectContext = serverRuntime.newContext();
            for(int i = 0; i < 100; i++) {
                Artist artist = objectContext.newObject(Artist.class);
                setArtistsFields(artist, "Test" + i, new Date(1000));
            }
        }

    }

    @Benchmark
    public ObjectContext contextCreation() {
        return serverRuntime.newContext();
    }

    @Benchmark
    public Artist objectCreation(ContextSetup contextSetup) {
        return contextSetup.objectContext.newObject(Artist.class);
    }

    @Benchmark
    public Artist settingFields(ObjectSetup objectSetup) {
        setArtistsFields(objectSetup.artist, "test", new Date(1000));
        return objectSetup.artist;
    }

    @Benchmark
    public void contextCommit(ObjectSetupWithFields objectSetupWithFields) {
        objectSetupWithFields.objectContext.commitChanges();
    }

    @Benchmark
    public void totalExecution() {
        ObjectContext context = serverRuntime
                .newContext();
        Artist artist = context.newObject(Artist.class);
        setArtistsFields(artist, "test", new Date(1000));
        context.commitChanges();
    }

    @Benchmark
    public void commitForManyObjects(SetupWithManyObjects setupWithManyObjects) {
        setupWithManyObjects.objectContext.commitChanges();
    }

    private static void setArtistsFields(Artist artist, String name, Date date) {
        artist.setId(1);
        artist.setName(name);
        artist.setDateOfBirth(date);
    }
}
