package org.apache.cayenne.experiment;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.DbAttribute;

public interface JoinContentVisitor<T> {

    T visit(DbAttribute[] source, DbAttribute[] target);

    T visit(DbAttribute source, DbAttribute target);

    default boolean visit(Expression expression){
        return true;
    }

}
