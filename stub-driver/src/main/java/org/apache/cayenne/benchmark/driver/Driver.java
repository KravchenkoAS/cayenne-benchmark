package org.apache.cayenne.benchmark.driver;

public class Driver extends StubDriver implements java.sql.Driver {
    //
    // Register ourselves with the DriverManager
    //
    static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (Exception E) {
            throw new RuntimeException("Can't register driver!");
        }
    }

    /**
     * Construct a new driver and register it with DriverManager
     *
     * @throws Exception
     *             if a database error occurs.
     */
    public Driver() throws Exception {
        // Required for Class.forName().newInstance()
    }
}
