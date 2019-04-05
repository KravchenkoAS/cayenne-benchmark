package org.apache.cayenne.experiment;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.util.XMLEncoder;

public class SinglePairCondition extends DbJoinCondition {

    private ColumnPair columnPair;

    public SinglePairCondition(ColumnPair columnPair) {
        this.columnPair = columnPair;
    }

    public ColumnPair getColumnPair() {
        return columnPair;
    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(columnPair);
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {

    }
}
