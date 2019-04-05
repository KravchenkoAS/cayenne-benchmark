package org.apache.cayenne.experiment;

import org.apache.cayenne.map.DbAttribute;

public class DirectedPairLeft implements DirectedPair {

    private ColumnPair columnPair;

    public DirectedPairLeft(ColumnPair columnPair) {
        this.columnPair = columnPair;
    }

    @Override
    public DbAttribute getSource() {
        return columnPair.getLeft();
    }

    @Override
    public DbAttribute getTarget() {
        return columnPair.getRight();
    }
}
