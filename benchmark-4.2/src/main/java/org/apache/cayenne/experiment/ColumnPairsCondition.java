package org.apache.cayenne.experiment;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.util.XMLEncoder;

public class ColumnPairsCondition extends DbJoinCondition {

    private List<ColumnPair> pairs;

    public ColumnPairsCondition() {
        this.pairs = new ArrayList<>();
    }

    public void addPair(ColumnPair columnPair) {
        this.pairs.add(columnPair);
    }

    public List<ColumnPair> getPairs() {
        return pairs;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {
    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(pairs);
    }
}
