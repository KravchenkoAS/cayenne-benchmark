package org.apache.cayenne.experiment;


public interface PairsConsumer {

    <T> T consume(JoinContentVisitor<T> joinContentVisitor);

}
