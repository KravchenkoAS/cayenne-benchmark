package org.apache.cayenne.benchmark.server;

import java.time.LocalDate;
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

@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 6, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
public class UpdateComponentsBenchmark {

    @State(Scope.Benchmark)
    public static class ObjectsSetup {

        ServerRuntime serverRuntime;
        ObjectContext context;
        List<Artist> artists;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            artists = ObjectSelect.query(Artist.class)
                    .select(context);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForOneObjectOneField {

        ServerRuntime serverRuntime;
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            List<Artist> artists = ObjectSelect.query(Artist.class)
                    .select(context);
            artists.get(0).setName("UpdateName");
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForOneObjectTwoField {

        ServerRuntime serverRuntime;
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            List<Artist> artists = ObjectSelect.query(Artist.class)
                    .select(context);
            artists.get(0).setName("UpdateName");
            artists.get(0).setDateOfBirth(LocalDate.of(2000, 2, 2));
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForHundredObjectsOneField {

        ServerRuntime serverRuntime;
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            List<Artist> artists = ObjectSelect.query(Artist.class)
                    .select(context);
            for(int i = 0; i < 100; i++) {
                artists.get(i).setName("UpdateName" + i);
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForHundredObjectsTwoField {

        ServerRuntime serverRuntime;
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            List<Artist> artists = ObjectSelect.query(Artist.class)
                    .select(context);
            for(int i = 0; i < 100; i++) {
                artists.get(i).setName("UpdateName" + i);
                artists.get(i).setDateOfBirth(LocalDate.of(i, 2, 2));
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForAllObjectsOneField {

        ServerRuntime serverRuntime;
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            List<Artist> artists = ObjectSelect.query(Artist.class)
                    .select(context);
            for(int i = 0; i < 1000; i++) {
                artists.get(i).setName("UpdateName" + i);
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForAllObjectsTwoField {

        ServerRuntime serverRuntime;
        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            serverRuntime = ServerRuntime.builder()
                    .addConfig("cayenne-project.xml")
                    .build();
            context = serverRuntime.newContext();
            List<Artist> artists = ObjectSelect.query(Artist.class)
                    .select(context);
            for(int i = 0; i < 1000; i++) {
                artists.get(i).setName("UpdateName" + i);
                artists.get(i).setDateOfBirth(LocalDate.of(i, 2, 2));
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            serverRuntime.shutdown();
        }
    }

    @Benchmark
    public List<Artist> updateOneObject(ObjectsSetup objectsSetup) {
        objectsSetup.artists.get(0).setName("UpdateName");
        return objectsSetup.artists;
    }

    @Benchmark
    public List<Artist> updateOneObjectTwoFields(ObjectsSetup objectsSetup) {
        objectsSetup.artists.get(0).setName("UpdateName");
        objectsSetup.artists.get(0).setDateOfBirth(LocalDate.of(2000, 2, 2));
        return objectsSetup.artists;
    }

    @Benchmark
    public List<Artist> updateHundredObject(ObjectsSetup objectsSetup) {
        for(int i = 0; i < 100; i++) {
            objectsSetup.artists.get(i).setName("UpdateName" + i);
        }
        return objectsSetup.artists;
    }

    @Benchmark
    public List<Artist> updateHundredObjectTwoFields(ObjectsSetup objectsSetup) {
        for(int i = 0; i < 100; i++) {
            objectsSetup.artists.get(i).setName("UpdateName" + i);
            objectsSetup.artists.get(i).setDateOfBirth(LocalDate.of(i, 2, 2));
        }
        return objectsSetup.artists;
    }

    @Benchmark
    public List<Artist> updateAllObject(ObjectsSetup objectsSetup) {
        for(int i = 0; i < 1000; i++) {
            objectsSetup.artists.get(i).setName("UpdateName" + i);
        }
        return objectsSetup.artists;
    }

    @Benchmark
    public List<Artist> updateAllObjectTwoFields(ObjectsSetup objectsSetup) {
        for(int i = 0; i < 1000; i++) {
            objectsSetup.artists.get(i).setName("UpdateName" + i);
            objectsSetup.artists.get(i).setDateOfBirth(LocalDate.of(i, 2, 2));
        }
        return objectsSetup.artists;
    }

    @Benchmark
    public void commitOneObjOneField(SetupForOneObjectOneField setupForOneObjectOneField) {
        setupForOneObjectOneField.context.commitChanges();
    }

    @Benchmark
    public void commitOneObjTwoField(SetupForOneObjectTwoField setupForOneObjectTwoField) {
        setupForOneObjectTwoField.context.commitChanges();
    }

    @Benchmark
    public void commit100ObjOneField(SetupForHundredObjectsOneField setupForHundredObjectsOneField) {
        setupForHundredObjectsOneField.context.commitChanges();
    }

    @Benchmark
    public void commit100ObjTwoField(SetupForHundredObjectsTwoField setupForHundredObjectsTwoField) {
        setupForHundredObjectsTwoField.context.commitChanges();
    }

    @Benchmark
    public void commit1000ObjOneField(SetupForAllObjectsOneField setupForAllObjectsOneField) {
        setupForAllObjectsOneField.context.commitChanges();
    }

    @Benchmark
    public void commit1000ObjTwoField(SetupForAllObjectsTwoField setupForAllObjectsTwoField) {
        setupForAllObjectsTwoField.context.commitChanges();
    }
}
