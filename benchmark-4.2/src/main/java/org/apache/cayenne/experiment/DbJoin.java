package org.apache.cayenne.experiment;

import java.io.Serializable;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.util.XMLEncoder;
import org.apache.cayenne.util.XMLSerializable;

public class DbJoin implements Serializable, XMLSerializable {

    private DbJoinCondition dbJoinCondition;
    private NewDbRelationship[] dbRelationships;

    public DbJoin(DbEntity srcEntity, DbEntity dstEntity) {
        this.dbRelationships = new NewDbRelationship[2];
        fillArray(srcEntity, dstEntity);
    }

    private void fillArray(DbEntity srcEntity, DbEntity dstEntity) {
        dbRelationships[RelationshipDirection.LEFT.ordinal()] =
                new NewDbRelationship(this, srcEntity, RelationshipDirection.LEFT);
        srcEntity.addRelationship(dbRelationships[RelationshipDirection.LEFT.ordinal()]);

        dbRelationships[RelationshipDirection.RIGHT.ordinal()] =
                new NewDbRelationship(this, dstEntity, RelationshipDirection.RIGHT);
        dstEntity.addRelationship(dbRelationships[RelationshipDirection.RIGHT.ordinal()]);
    }

    public void setCondition(DbJoinCondition dbJoinCondition) {
        this.dbJoinCondition = dbJoinCondition;
        for(NewDbRelationship newDbRelationship : dbRelationships) {
            newDbRelationship.buildPairs();
        }
    }

    public DbEntity getTargetEntity(RelationshipDirection direction) {
        return dbRelationships[direction.getOppositeDirection().ordinal()]
                .getSourceEntity();
    }

    public NewDbRelationship getReverseRelationship(RelationshipDirection direction) {
        return dbRelationships[direction.getOppositeDirection().ordinal()];
    }

    public DbJoinCondition getDbJoinCondition() {
        return dbJoinCondition;
    }

    public NewDbRelationship[] getRelationships() {
        return dbRelationships;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {

    }
}
