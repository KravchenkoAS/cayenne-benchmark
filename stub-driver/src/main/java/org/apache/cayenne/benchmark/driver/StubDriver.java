package org.apache.cayenne.benchmark.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class StubDriver implements Driver {
    public Connection connect(String url, Properties info) throws SQLException {
        return StubConnection.getInstance();
    }

    public boolean acceptsURL(String url) throws SQLException {
        return true;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return 0;
    }

    public int getMinorVersion() {
        return 1;
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getGlobal();
    }
}
