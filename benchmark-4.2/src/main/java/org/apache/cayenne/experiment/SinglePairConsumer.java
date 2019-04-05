package org.apache.cayenne.experiment;

public class SinglePairConsumer implements PairsConsumer {

    private DirectedPair directedPair;

    public SinglePairConsumer(DirectedPair directedPair) {
        this.directedPair = directedPair;
    }

    @Override
    public <T> T consume(JoinContentVisitor<T> joinContentVisitor) {
        return joinContentVisitor.visit(directedPair.getSource(), directedPair.getTarget());
    }
}
