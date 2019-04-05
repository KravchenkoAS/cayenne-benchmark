package org.apache.cayenne.experiment;

public enum RelationshipDirection {

    LEFT{
        @Override
        public RelationshipDirection getOppositeDirection() {
            return RIGHT;
        }
    },
    RIGHT {
        @Override
        public RelationshipDirection getOppositeDirection() {
            return LEFT;
        }
    };

    public abstract RelationshipDirection getOppositeDirection();
}
