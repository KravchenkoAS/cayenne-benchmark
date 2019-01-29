package org.apache.cayenne.stable.benchmark.server;

import java.sql.Date;
import java.time.LocalDate;
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

@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SetFieldsBenchmark {

    private static ServerRuntime serverRuntime;

    @Setup(Level.Iteration)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> ServerModule.contributeProperties(binder)
                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(false)))
                .addModule(binder -> binder.bind(EventManager.class).toInstance(new NoopEventManager()))
                .build();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @State(Scope.Benchmark)
    public static class ContextSetup {
        ObjectContext context;
        Date localDate;
        Artist artist;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            localDate = new Date(1000);
            artist = new Artist();
        }

    }

    @State(Scope.Benchmark)
    public static class ObjectSetup {

        ObjectContext context;
        Artist artist;
        Date localDate;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artist = context.newObject(Artist.class);
            localDate = new Date(1000);
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetup1S {

        ObjectContext context;
        Artist artist;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artist = context.newObject(Artist.class);
            setOneSetter(artist);
        }

    }

    @State(Scope.Benchmark)
    public static class ObjectSetup2S {

        ObjectContext context;
        Artist artist;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artist = context.newObject(Artist.class);
            setTwoSetters(artist);
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetup3S {

        ObjectContext context;
        Artist artist;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            artist = context.newObject(Artist.class);
            setThreeSetters(artist, new Date(1000));
        }
    }

    @Benchmark
    public Artist create1Setter(ContextSetup contextSetup) {
        Artist artist = contextSetup.context.newObject(Artist.class);
        setOneSetter(artist);
        return artist;
    }

    @Benchmark
    public Artist create2Setters(ContextSetup contextSetup) {
        Artist artist = contextSetup.context.newObject(Artist.class);
        setTwoSetters(artist);
        return artist;
    }

    @Benchmark
    public Artist create3Setters(ContextSetup contextSetup) {
        Artist artist = contextSetup.context.newObject(Artist.class);
        setThreeSetters(artist, contextSetup.localDate);
        return artist;
    }

    @Benchmark
    public void oneSetter(ObjectSetup objectSetup) {
        setOneSetter(objectSetup.artist);
    }

    @Benchmark
    public void twoSetters(ObjectSetup objectSetup) {
        setTwoSetters(objectSetup.artist);
    }

    @Benchmark
    public void threeSetters(ObjectSetup objectSetup) {
        setThreeSetters(objectSetup.artist, objectSetup.localDate);
    }

    @Benchmark
    public void commit1S(ObjectSetup1S objectSetup1S) {
        objectSetup1S.context.commitChanges();
    }

    @Benchmark
    public void commit2S(ObjectSetup2S objectSetup2S) {
        objectSetup2S.context.commitChanges();
    }

    @Benchmark
    public void commit3S(ObjectSetup3S objectSetup3S) {
        objectSetup3S.context.commitChanges();
    }

    @Benchmark
    public void setPojo(ContextSetup contextSetup) {
        contextSetup.artist.setId(1);
    }

    private static void setOneSetter(Artist artist) {
        artist.setId(1);
    }

    private static void setTwoSetters(Artist artist) {
        artist.setId(1);
        artist.setName("test");
    }

    private static void setThreeSetters(Artist artist, Date date) {
        artist.setId(1);
        artist.setName("test");
        artist.setDateOfBirth(date);
    }
}
