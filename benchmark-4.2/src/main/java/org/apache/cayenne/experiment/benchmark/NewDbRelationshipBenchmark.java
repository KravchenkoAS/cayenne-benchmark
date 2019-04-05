package org.apache.cayenne.experiment.benchmark;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.experiment.ColumnPair;
import org.apache.cayenne.experiment.ColumnPairsCondition;
import org.apache.cayenne.experiment.DbJoin;
import org.apache.cayenne.experiment.NewDbRelationship;
import org.apache.cayenne.experiment.SinglePairCondition;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
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
@Measurement(iterations = 4, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class NewDbRelationshipBenchmark {

    private NewDbRelationship[] relationships;
    private Map<String, Object> map;

    @Setup(Level.Iteration)
    public void setUp() {
        DbEntity entity1 = new DbEntity("entity1");
        DbAttribute attr1 = new DbAttribute("attr1");
        attr1.setEntity(entity1);

        DbAttribute attr1A = new DbAttribute("attr1A");
//        attr1A.setEntity(entity1);
        entity1.addAttribute(attr1);
//        entity1.addAttribute(attr1A);

        DbEntity entity2 = new DbEntity("entity2");
        DbAttribute attr2 = new DbAttribute("attr2");
        attr2.setEntity(entity2);
        attr2.setPrimaryKey(true);
        attr2.setMandatory(true);

        DbAttribute attr2A = new DbAttribute("attr2A");
        attr2A.setPrimaryKey(true);
//        attr2A.setEntity(entity2);
        entity2.addAttribute(attr2);
//        entity2.addAttribute(attr2A);

        DbJoin dbJoin = new DbJoin(entity1, entity2);

        SinglePairCondition dbJoinCondition = new SinglePairCondition(new ColumnPair(attr1, attr2));
//        ColumnPairsCondition dbJoinCondition = new ColumnPairsCondition();
//        dbJoinCondition.addPair(new ColumnPair(attr1, attr2));
//        dbJoinCondition.addPair(new ColumnPair(attr1A, attr2A));

        dbJoin.setCondition(dbJoinCondition);

        relationships = dbJoin.getRelationships();

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
        return relationships[0].getSourceEntity();
    }

    @Benchmark
    public Relationship getReverseRelationship() {
        return relationships[0].getReverseRelationship();
    }

    @Benchmark
    public boolean isToPk() {
        return relationships[0].isToPK();
    }

    @Benchmark
    public boolean isFromPk() {
        return relationships[0].isFromPK();
    }

    @Benchmark
    public boolean isMandatory() {
        return relationships[0].isMandatory();
    }

    @Benchmark
    public boolean isToMasterPk() {
        return relationships[0].isToMasterPK();
    }

    @Benchmark
    public boolean isSourceIndependentFromTargetChange() {
        return relationships[0].isSourceIndependentFromTargetChange();
    }

    @Benchmark
    public Map<String, Object> srcFkSnapshotWithTargetSnapshot() {
        return relationships[1].srcFkSnapshotWithTargetSnapshot(map);
    }

    @Benchmark
    public Collection<DbAttribute> getSourceAttributes() {
        return relationships[0].getSourceAttributes();
    }

    @Benchmark
    public Collection<DbAttribute> getTargetAttributes() {
        return relationships[1].getTargetAttributes();
    }

    @Benchmark
    public Map<String, Object> targetPkSnapshotWithSrcSnapshot() {
        return relationships[0]
                .targetPkSnapshotWithSrcSnapshot(map);
    }
}
