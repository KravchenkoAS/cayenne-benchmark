package org.apache.cayenne.benchmark.driver;

import java.sql.SQLException;
import java.sql.Savepoint;

public class StubSavepoint implements Savepoint {

    private String savepointName = "Bootique stub savepoint name.";

    public StubSavepoint() {
    }

    public StubSavepoint(String savepointName) {
        this.savepointName = savepointName;
    }


    @Override
    public int getSavepointId() throws SQLException {
        return 0;
    }

    @Override
    public String getSavepointName() throws SQLException {
        return savepointName;
    }
}
