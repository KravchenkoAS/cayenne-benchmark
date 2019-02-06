package org.apache.cayenne.benchmark.driver;

import javax.sql.rowset.RowSetMetaDataImpl;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Map;

public class StubResultSetMetadata extends RowSetMetaDataImpl {
    public StubResultSetMetadata(Map<Integer, String> fields) throws SQLException {
        super();
        super.setColumnCount(fields.size());
        for(Integer key : fields.keySet()) {
            String columnName = fields.get(key);
            super.setColumnName(key, columnName);
        }
    }


}
