package org.apache.cayenne.benchmark.server;

import java.time.LocalDate;
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

@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
public class WriteComponentsBenchmark {

    @State(Scope.Benchmark)
    public static class RuntimeSetup {

        ServerRuntime serverRuntime;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class ContextSetup {

        ServerRuntime serverRuntime;
        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            objectContext = serverRuntime.newContext();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetup {

        ServerRuntime serverRuntime;
        ObjectContext objectContext;
        Artist artist;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            objectContext = serverRuntime.newContext();
            artist = objectContext.newObject(Artist.class);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class ObjectSetupWithFields {

        ServerRuntime serverRuntime;
        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            objectContext = serverRuntime.newContext();
            Artist artist = objectContext.newObject(Artist.class);
            artist.setId(1);
            artist.setName("Test");
            artist.setDateOfBirth(LocalDate.now());
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupWithManyObjects {

        ServerRuntime serverRuntime;
        ObjectContext objectContext;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            objectContext = serverRuntime.newContext();
            for(int i = 0; i < 100; i++) {
                Artist artist = objectContext.newObject(Artist.class);
                artist.setId(i);
                artist.setName("Test" + i);
                artist.setDateOfBirth(LocalDate.now());
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @Benchmark
    public ObjectContext contextCreation(RuntimeSetup runtimeSetup) {
        return runtimeSetup.serverRuntime.newContext();
    }

    @Benchmark
    public Artist objectCreation(ContextSetup contextSetup) {
        return contextSetup.objectContext.newObject(Artist.class);
    }

    @Benchmark
    public Artist settingFields(ObjectSetup objectSetup) {
        objectSetup.artist.setId(1);
        objectSetup.artist.setName("Test");
        objectSetup.artist.setDateOfBirth(LocalDate.now());
        return objectSetup.artist;
    }

    @Benchmark
    public void contextCommit(ObjectSetupWithFields objectSetupWithFields) {
        objectSetupWithFields.objectContext.commitChanges();
    }

    @Benchmark
    public void totalExecution(RuntimeSetup runtimeSetup) {
        ObjectContext context = runtimeSetup.serverRuntime
                .newContext();
        Artist artist = context.newObject(Artist.class);
        artist.setId(1);
        artist.setName("Test");
        artist.setDateOfBirth(LocalDate.now());
        context.commitChanges();
    }

    @Benchmark
    public void commitForManyObjects(SetupWithManyObjects setupWithManyObjects) {
        setupWithManyObjects.objectContext.commitChanges();
    }
}
