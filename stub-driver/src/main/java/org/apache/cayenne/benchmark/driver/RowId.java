package org.apache.cayenne.benchmark.driver;

public class RowId implements java.sql.RowId {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
