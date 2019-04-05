package org.apache.cayenne.experiment;

import java.io.Serializable;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.util.XMLEncoder;
import org.apache.cayenne.util.XMLSerializable;


public class ColumnPair implements Serializable, XMLSerializable {

    private DbAttribute left;
    private DbAttribute right;

    public ColumnPair(DbAttribute left, DbAttribute right) {
        this.left = left;
        this.right = right;
    }

    public DbAttribute getLeft() {
        return left;
    }

    public DbAttribute getRight() {
        return right;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {

    }
}

