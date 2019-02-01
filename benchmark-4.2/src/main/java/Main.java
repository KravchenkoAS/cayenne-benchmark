import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.reflect.ClassDescriptor;
import persistent.Painting;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        ObjectContext objectContext = serverRuntime.newContext();
        List<DataRow> dataRowList = ObjectSelect.dataRowQuery(Painting.class)
                .select(objectContext);
        ClassDescriptor descriptor = objectContext.getEntityResolver().getClassDescriptor("Painting");
        List<Painting> paintings = ((DataContext) objectContext).objectsFromDataRows(descriptor, dataRowList);
        System.out.println();
    }
}
