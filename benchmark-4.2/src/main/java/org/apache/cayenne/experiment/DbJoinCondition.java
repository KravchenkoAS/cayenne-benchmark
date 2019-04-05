package org.apache.cayenne.experiment;

import java.io.Serializable;

import org.apache.cayenne.util.XMLSerializable;

public abstract class DbJoinCondition implements Serializable, XMLSerializable {

    public abstract<T> T accept(JoinVisitor<T> joinVisitor);

}
