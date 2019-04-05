package org.apache.cayenne.experiment.benchmark;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;
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

@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class DbRelationshipBenchmark {

    private DbRelationship relationship;
    private DbRelationship reverseRelationship;

    private Map<String, Object> map;

    @Setup(Level.Iteration)
    public void setUp() {
        DataMap dataMap = new DataMap();

        DbEntity entity1 = new DbEntity("entity1");
        DbAttribute attr1 = new DbAttribute("attr1");
        attr1.setEntity(entity1);

        DbAttribute attr1A = new DbAttribute("attr1A");
        attr1A.setEntity(entity1);

        entity1.addAttribute(attr1);
        entity1.addAttribute(attr1A);

        DbEntity entity2 = new DbEntity("entity2");
        DbAttribute attr2 = new DbAttribute("attr2");
        attr2.setEntity(entity2);
        attr2.setPrimaryKey(true);
        attr2.setMandatory(true);

        DbAttribute attr2A = new DbAttribute("attr2A");
        attr2A.setPrimaryKey(true);
        attr2A.setEntity(entity2);
        entity2.addAttribute(attr2);
        entity2.addAttribute(attr2A);

        relationship = new DbRelationship("rel");
        relationship.setSourceEntity(entity1);
        relationship.setTargetEntityName(entity2);
        relationship.addJoin(new DbJoin(relationship, "attr1", "attr2"));
//        relationship.addJoin(new DbJoin(relationship, "attr1A", "attr2A"));
        entity1.addRelationship(relationship);

        reverseRelationship = new DbRelationship("revRel");
        reverseRelationship.setSourceEntity(entity2);
        reverseRelationship.setTargetEntityName(entity1);
        reverseRelationship.addJoin(new DbJoin(reverseRelationship, "attr2", "attr1"));
//        reverseRelationship.addJoin(new DbJoin(reverseRelationship, "attr2A", "attr1A"));
        entity2.addRelationship(reverseRelationship);

        dataMap.addDbEntity(entity1);
        dataMap.addDbEntity(entity2);

        map = new HashMap<>();
        Integer id = 44;
        map.put("attr1", id);
//        map.put("attr1A", id);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
    }

    @Benchmark
    public Entity getSourceEntity() {
        return relationship.getSourceEntity();
    }

    @Benchmark
    public Relationship getReverseRelationship() {
        return relationship.getReverseRelationship();
    }

    @Benchmark
    public boolean isToPk() {
        return relationship.isToPK();
    }

    @Benchmark
    public boolean isFromPk() {
        return relationship.isFromPK();
    }

    @Benchmark
    public boolean isMandatory() {
        return relationship.isMandatory();
    }

    @Benchmark
    public boolean isToMasterPk() {
        return relationship.isToMasterPK();
    }

    @Benchmark
    public boolean isSourceIndependentFromTargetChange() {
        return relationship.isSourceIndependentFromTargetChange();
    }

    @Benchmark
    public Map<String, Object> srcFkSnapshotWithTargetSnapshot() {
        return reverseRelationship.srcFkSnapshotWithTargetSnapshot(map);
    }

    @Benchmark
    public Collection<DbAttribute> getSourceAttributes() {
        return relationship.getSourceAttributes();
    }

    @Benchmark
    public Collection<DbAttribute> getTargetAttributes() {
        return relationship.getTargetAttributes();
    }

    @Benchmark
    public Map<String, Object> targetPkSnapshotWithSrcSnapshot() {
        return relationship
                .targetPkSnapshotWithSrcSnapshot(map);
    }
}
