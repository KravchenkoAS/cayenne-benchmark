package org.apache.cayenne.experiment;

import org.apache.cayenne.map.DbAttribute;

public interface DirectedPair {

    DbAttribute getSource();

    DbAttribute getTarget();
}
