import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import persistent.Artist;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerRuntime serverRuntime = ServerRuntime.builder().build();
        ObjectContext context = serverRuntime.newContext();
    }
}
