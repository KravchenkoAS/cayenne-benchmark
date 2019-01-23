package org.apache.cayenne.benchmark.driver;

import java.sql.SQLException;
import java.util.Map;

public class Struct implements java.sql.Struct {
    @Override
    public String getSQLTypeName() throws SQLException {
        return "Stub";
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        return new Object[0];
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        return new Object[0];
    }
}
