package org.apache.cayenne.experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.util.XMLEncoder;

public class NewDbRelationship extends Relationship {

    private DbJoin dbJoin;
    private RelationshipDirection direction;

    private PairsConsumer pairsConsumer;

    protected boolean toDependentPK;

    public NewDbRelationship() {
        super();
    }

    public NewDbRelationship(String name) {
        super(name);
    }

    public NewDbRelationship(DbJoin dbJoin, DbEntity entity, RelationshipDirection direction) {
        this.dbJoin = dbJoin;
        this.sourceEntity = entity;
        this.direction = direction;
        this.name = entity.getName() + "Rel";
    }

    public void buildPairs() {
        this.pairsConsumer = dbJoin.getDbJoinCondition().accept(new JoinVisitor<PairsConsumer>() {

            @Override
            public PairsConsumer visit(ColumnPair columnPair) {
                return new SinglePairConsumer(direction == RelationshipDirection.LEFT ?
                        new DirectedPairLeft(columnPair) :
                        new DirectedPairRight(columnPair));
            }

            @Override
            public PairsConsumer visit(List<ColumnPair> columnPairList) {
                List<DirectedPair> directedPairs = new ArrayList<>();
                for(ColumnPair columnPair : columnPairList) {
                    directedPairs.add(direction == RelationshipDirection.LEFT ?
                            new DirectedPairLeft(columnPair) :
                            new DirectedPairRight(columnPair));
                }
                return new ListPairsConsumer(directedPairs);
            }
        });
    }

    public <T> T accept(JoinContentVisitor<T> joinContentVisitor) {
        return pairsConsumer.consume(joinContentVisitor);
    }

    @Override
    public DbEntity getSourceEntity() {
        return (DbEntity) super.getSourceEntity();
    }

    @Override
    public Entity getTargetEntity() {
        return dbJoin.getTargetEntity(direction);
    }

    @Override
    public NewDbRelationship getReverseRelationship() {
        return dbJoin.getReverseRelationship(direction);
    }

    @Override
    public boolean isMandatory() {
        boolean mandatoryNotFound = accept(new JoinContentVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] source, DbAttribute[] target) {
                for(DbAttribute dbAttribute : source) {
                    if(dbAttribute.isMandatory()) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public Boolean visit(DbAttribute source, DbAttribute target) {
                return !source.isMandatory();
            }
        });
        return !mandatoryNotFound;
    }

    public boolean isToDependentPK() {
        return toDependentPK;
    }

    public void setToDependentPK(boolean toDependentPK) {
        this.toDependentPK = toDependentPK;
    }

    public void setToMany(boolean toMany) {
        this.toMany = toMany;
    }

    public boolean isToPK() {
        return accept(new JoinContentVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] source, DbAttribute[] target) {
                for (DbAttribute attribute : target) {
                    if (attribute == null) {
                        return false;
                    }

                    if (!attribute.isPrimaryKey()) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public Boolean visit(DbAttribute source, DbAttribute target) {
                if (target == null) {
                    return false;
                }
                return target.isPrimaryKey();
            }
        });
    }

    public boolean isFromPK() {
        return accept(new JoinContentVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] src, DbAttribute[] target) {
                for (DbAttribute attribute : src) {
                    if (attribute == null) {
                        return false;
                    }

                    if (attribute.isPrimaryKey()) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public Boolean visit(DbAttribute src, DbAttribute target) {
                if (src == null) {
                    return false;
                }

                return src.isPrimaryKey();
            }
        });
    }

    public boolean isToMasterPK() {
        return !(isToMany() || isToDependentPK()) &&
                getReverseRelationship().isToDependentPK();
    }

    public boolean isSourceIndependentFromTargetChange() {
        // note - call "isToPK" at the end of the chain, since
        // if it is to a dependent PK, we still should return true...
        return isToMany() || isToDependentPK() || !isToPK();
    }

    public Map<String, Object> srcFkSnapshotWithTargetSnapshot(Map<String, Object> targetSnapshot) {

        if (isToMany()) {
            throw new CayenneRuntimeException("Only 'to one' relationships support this method.");
        }

        return srcSnapshotWithTargetSnapshot(targetSnapshot);
    }

    private Map<String, Object> srcSnapshotWithTargetSnapshot(Map<String, Object> targetSnapshot) {
        return accept(new MapJoinContentVisitorSrc(targetSnapshot));
    }

    public Collection<DbAttribute> getTargetAttributes() {
        return mapJoinsToAttributes(false);
    }

    /**
     * Returns a Collection of source attributes.
     *
     * @since 1.1
     */
    public Collection<DbAttribute> getSourceAttributes() {
        return mapJoinsToAttributes(true);
    }

    private Collection<DbAttribute> mapJoinsToAttributes(boolean getSource) {
        return accept(new JoinContentVisitor<List<DbAttribute>>() {
            @Override
            public List<DbAttribute> visit(DbAttribute[] src, DbAttribute[] target) {
                return getSource ? Arrays.asList(src) : Arrays.asList(target);
            }

            @Override
            public List<DbAttribute> visit(DbAttribute src, DbAttribute target) {
                return Collections.singletonList(getSource ? src : target);
            }
        });
    }

    public Map<String, Object> targetPkSnapshotWithSrcSnapshot(Map<String, Object> srcSnapshot) {

        if (isToMany()) {
            throw new CayenneRuntimeException("Only 'to one' relationships support this method.");
        }

        return accept(new MapJoinContentVisitorTarget(srcSnapshot));
    }

    public String toString() {
        StringBuilder res = new StringBuilder("Db Relationship : ");
        res.append(toMany ? "toMany" : "toOne ");

        String sourceEntityName = sourceEntity.getName();
        return accept(new JoinContentVisitor<StringBuilder>() {
            @Override
            public StringBuilder visit(DbAttribute[] src, DbAttribute[] target) {
                for(int i = 0; i < src.length; i++) {
                    res.append(" (")
                            .append(sourceEntityName).append(".")
                            .append(src[i].getName()).append(", ")
                            .append(getTargetEntity().getName()).append(".")
                            .append(target[i].getName()).append(")");
                }

                return res;
            }

            @Override
            public StringBuilder visit(DbAttribute src, DbAttribute target) {
                return res.append(" (").append(sourceEntityName)
                        .append(".").append(src.getName())
                        .append(", ").append(getTargetEntity().getName()).append(".")
                        .append(target.getName()).append(")");
            }
        }).toString();
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {
    }

    private static class MapJoinContentVisitorTarget implements JoinContentVisitor<Map<String, Object>> {
        private final Map<String, Object> srcSnapshot;

        MapJoinContentVisitorTarget(Map<String, Object> srcSnapshot) {
            this.srcSnapshot = srcSnapshot;
        }

        @Override
        public Map<String, Object> visit(DbAttribute[] src, DbAttribute[] target) {
            int size = src.length;
            int foundNulls = 0;
            Map<String, Object> resMap = new HashMap<>(size * 2);
            for(int i = 0; i < size; i++) {
                Object val = srcSnapshot.get(src[i].getName());
                if (val == null) {
                    // some keys may be nulls and some not in case of multi-key
                    // relationships where PK and FK partially overlap (see
                    // CAY-284)
                    if (!src[i].isMandatory()) {
                        return null;
                    }
                    foundNulls++;
                } else {
                    resMap.put(target[i].getName(), val);
                }
            }

            if(foundNulls == 0) {
                return resMap;
            } else if(foundNulls == size) {
                return null;
            } else {
                throw new CayenneRuntimeException("Some parts of FK are missing in snapshot, relationship: %s", this);
            }
        }

        @Override
        public Map<String, Object> visit(DbAttribute source, DbAttribute target) {
            Object val = srcSnapshot.get(source.getName());
            return val != null ?
                    Collections.singletonMap(target.getName(), val) :
                    null;
        }
    }

    private static class MapJoinContentVisitorSrc implements JoinContentVisitor<Map<String, Object>> {
        private final Map<String, Object> targetSnapshot;

        public MapJoinContentVisitorSrc(Map<String, Object> targetSnapshot) {
            this.targetSnapshot = targetSnapshot;
        }

        @Override
        public Map<String, Object> visit(DbAttribute[] src, DbAttribute[] target) {
            int size = src.length;
            Map<String, Object> idMap = new HashMap<>(size * 2);
            for(int i = 0; i < size; i++) {
                Object val = targetSnapshot.get(target[i].getName());
                idMap.put(src[i].getName(), val);
            }

            return idMap;
        }

        @Override
        public Map<String, Object> visit(DbAttribute src, DbAttribute target) {
            return Collections.singletonMap(src.getName(),
                    targetSnapshot.get(target.getName()));
        }
    }
}
