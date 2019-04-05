package org.apache.cayenne.experiment.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.experiment.ColumnPair;
import org.apache.cayenne.experiment.DirectedPairLeft;
import org.apache.cayenne.experiment.JoinContentVisitor;
import org.apache.cayenne.experiment.PairsConsumer;
import org.apache.cayenne.experiment.SinglePairConsumer;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
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
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class NewDbRelationshipsPartsBenchmark {

    JoinContentVisitor<List<DbAttribute>> visitor;

    List<ColumnPair> pairs;

    PairsConsumer pairsConsumer;

    private Map<String, Object> map;

    @Setup(Level.Iteration)
    public void setUp() {
        visitor = new JoinContentVisitor<List<DbAttribute>>() {
            @Override
            public List<DbAttribute> visit(DbAttribute[] source, DbAttribute[] target) {
                return null;
            }

            @Override
            public List<DbAttribute> visit(DbAttribute source, DbAttribute target) {
                return null;
            }
        };
        this.pairs = new ArrayList<>();
        DbEntity dbEntity1 = new DbEntity("entity1");
        DbEntity dbEntity2 = new DbEntity("entity2");
        DbAttribute attr1 = new DbAttribute("a");
        DbAttribute attr2 = new DbAttribute("b");
        DbAttribute attr3 = new DbAttribute("c");
        DbAttribute attr4 = new DbAttribute("d");
        dbEntity1.addAttribute(attr1);
        dbEntity1.addAttribute(attr2);
        dbEntity2.addAttribute(attr3);
        dbEntity2.addAttribute(attr4);

        pairs.add(new ColumnPair(attr1, attr3));
        pairs.add(new ColumnPair(attr2, attr4));

        map = new HashMap<>();
        Integer id = 44;
        map.put("attr1", id);
        map.put("attr1A", id);

        pairsConsumer = new SinglePairConsumer(new DirectedPairLeft(new ColumnPair(attr1, attr3)));
    }

    @Benchmark
    public Object consumerBenchmark() {
        return pairsConsumer.consume(visitor);
    }

    @Benchmark
    public JoinContentVisitor<Map<String, Object>> joinContentVisitorBenchmark() {
        return new JoinContentVisitor<Map<String, Object>>() {
            @Override
            public Map<String, Object> visit(DbAttribute[] source, DbAttribute[] target) {
                Map<String, Object> idMap = new HashMap<>(source.length * 2);
                for(int i = 0; i < source.length; i++) {
                    Object val = map.get(target[i].getName());
                    idMap.put(source[i].getName(), val);
                }
                return idMap;
            }

            @Override
            public Map<String, Object> visit(DbAttribute source, DbAttribute target) {
                return Collections.singletonMap(source.getName(),
                        map.get(target.getName()));
            }
        };
    }
}
