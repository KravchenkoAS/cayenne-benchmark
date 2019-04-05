package org.apache.cayenne.experiment;

import java.util.List;

import org.apache.cayenne.exp.Expression;

public interface JoinVisitor<T> {

    T visit(ColumnPair columnPair);

    T visit(List<ColumnPair> columnPairList);

    default T visit(Expression expression){
        return null;
    }

}
