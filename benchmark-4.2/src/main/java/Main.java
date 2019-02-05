import java.sql.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import persistent.Artist;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerRuntime serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .build();
        ObjectContext objectContext = serverRuntime.newContext();
        List<Artist> artistList = ObjectSelect.query(Artist.class)
                .select(objectContext);

        artistList.get(0).setName("test-update");
        artistList.get(0).setDateOfBirth(new Date(1200));
//        Artist artist = objectContext.newObject(Artist.class);
//        artist.setId(1);
//        artist.setName("test-name");
//        artist.setDateOfBirth(new Date(1200));
        objectContext.commitChanges();

        String a = System.getProperty("objectsNumber");
        Integer argument = a != null ? Integer.valueOf(a) : 1;
        System.out.println(argument);
    }
}
