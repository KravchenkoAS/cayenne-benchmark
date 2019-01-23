package org.apache.cayenne.benchmark.driver;

import java.sql.SQLException;
import java.util.Map;

public class Ref implements java.sql.Ref {
    @Override
    public String getBaseTypeName() throws SQLException {
        return null;
    }

    @Override
    public Object getObject(Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Object getObject() throws SQLException {
        return null;
    }

    @Override
    public void setObject(Object value) throws SQLException {

    }
}
