package org.apache.cayenne.access.util;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.event.EventManager;
import org.apache.cayenne.graph.GraphChangeHandler;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.query.Query;

public class FakeDataChannel implements DataChannel {

    @Override
    public EventManager getEventManager() {
        return null;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
    public QueryResponse onQuery(ObjectContext originatingContext, Query query) {
        return new FakeQueryResponse();
    }

    @Override
    public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType) {
        return new GraphDiff() {
            @Override
            public boolean isNoop() {
                return false;
            }

            @Override
            public void apply(GraphChangeHandler handler) {

            }

            @Override
            public void undo(GraphChangeHandler handler) {

            }
        };
    }
}
