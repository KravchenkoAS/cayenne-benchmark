import java.util.Collection;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.QueryResult;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectById;
import org.apache.cayenne.query.SelectQuery;
import persistent.Artist;
import persistent.Gallery;
import persistent.Painting;

public class Main {
    public static void main(String[] args) {
        ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        ObjectContext context = serverRuntime.newContext();
    }
}
