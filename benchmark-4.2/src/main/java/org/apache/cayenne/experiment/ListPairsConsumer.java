package org.apache.cayenne.experiment;

import java.util.List;

import org.apache.cayenne.map.DbAttribute;

public class ListPairsConsumer implements PairsConsumer {

    private List<DirectedPair> directedPairs;

    private DbAttribute[] src;
    private DbAttribute[] target;

    public ListPairsConsumer(List<DirectedPair> directedPairs) {
        this.directedPairs = directedPairs;
        this.src = new DbAttribute[directedPairs.size()];
        this.target = new DbAttribute[directedPairs.size()];
        for(int i = 0; i < directedPairs.size(); i++) {
            this.src[i] = directedPairs.get(i).getSource();
            this.target[i] = directedPairs.get(i).getTarget();
        }
    }

    @Override
    public <T> T consume(JoinContentVisitor<T> joinContentVisitor) {
        return joinContentVisitor.visit(src, target);
    }
}
