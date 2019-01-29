package org.apache.cayenne.benchmark.server;

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
    public static class SetupForOneObjectOneField {

        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateOneField(context, 1);
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForOneObjectTwoField {

        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateTwoFields(context, 1);
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForHundredObjectsOneField {

        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateOneField(context, 100);
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForHundredObjectsTwoField {

        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateTwoFields(context, 100);
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForAllObjectsOneField {

        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists = getArtistsForUpdateOneField(context, 1000);
        }
    }

    @State(Scope.Benchmark)
    public static class SetupForAllObjectsTwoField {

        ObjectContext context;

        @Setup(Level.Invocation)
        public void setUp() {
            context = serverRuntime.newContext();
            List<Artist> artists =  getArtistsForUpdateTwoFields(context, 1000);
        }
    }

    @Benchmark
    public List<Artist> updateOneObject(ObjectsSetup objectsSetup) {
        return updateArtistsName(objectsSetup.artists, 1);
    }

    @Benchmark
    public List<Artist> updateOneObjectTwoFields(ObjectsSetup objectsSetup) {
        return updateArtistsNameAndDate(objectsSetup.artists,1);
    }

    @Benchmark
    public List<Artist> updateHundredObject(ObjectsSetup objectsSetup) {
        return updateArtistsName(objectsSetup.artists, 100);
    }

    @Benchmark
    public List<Artist> updateHundredObjectTwoFields(ObjectsSetup objectsSetup) {
        return updateArtistsNameAndDate(objectsSetup.artists,100);
    }

    @Benchmark
    public List<Artist> updateAllObject(ObjectsSetup objectsSetup) {
        return updateArtistsName(objectsSetup.artists, 1000);
    }

    @Benchmark
    public List<Artist> updateAllObjectTwoFields(ObjectsSetup objectsSetup) {
        return updateArtistsNameAndDate(objectsSetup.artists,1000);
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

    private static List<Artist> updateArtistsName(List<Artist> artists, int size) {
        for(int i = 0; i < size; i++) {
            artists.get(i).setName("UpdateName" + i);
        }
        return artists;
    }

    private static List<Artist> updateArtistsNameAndDate(List<Artist> artists, int size) {
        for(int i = 0; i < size; i++) {
            artists.get(i).setName("UpdateName" + i);
            artists.get(i).setDateOfBirth(new Date(1000));
        }
        return artists;
    }

    private static List<Artist> getArtistsForUpdateOneField(ObjectContext context, int size) {
        return updateArtistsName(ObjectSelect.query(Artist.class).select(context), size);
    }

    private static List<Artist> getArtistsForUpdateTwoFields(ObjectContext context, int size) {
        return updateArtistsNameAndDate(ObjectSelect.query(Artist.class).select(context), size);
    }
}
