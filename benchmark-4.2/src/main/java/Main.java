import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SQLSelect;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        ObjectContext objectContext = serverRuntime.newContext();

        List<Object[]> list = SQLSelect.scalarQuery("SELECT * FROM ARTIST")
                .select(objectContext);
    }
}
