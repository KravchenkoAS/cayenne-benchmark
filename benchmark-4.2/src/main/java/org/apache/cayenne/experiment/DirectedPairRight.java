package org.apache.cayenne.experiment;

import org.apache.cayenne.map.DbAttribute;

public class DirectedPairRight implements DirectedPair {

    private ColumnPair columnPair;

    public DirectedPairRight(ColumnPair columnPair) {
        this.columnPair = columnPair;
    }

    @Override
    public DbAttribute getSource() {
        return columnPair.getRight();
    }

    @Override
    public DbAttribute getTarget() {
        return columnPair.getLeft();
    }
}
