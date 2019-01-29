package org.apache.cayenne.benchmark.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.OperationObserver;
import org.apache.cayenne.access.jdbc.SelectAction;
import org.apache.cayenne.benchmark.event.utils.FakeOperationObserver;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SelectQuery;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import persistent.Artist;
import persistent.Painting;

@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 6, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SelectActionBenchmark {

    private ServerRuntime serverRuntime;
    private ObjectContext objectContext;
    private SelectQuery<Painting> query;
    private DataNode dataNode;
    private OperationObserver operationObserver;
    private Connection connection;

    @Setup(Level.Trial)
    public void setUp() throws SQLException {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> ServerModule.contributeProperties(binder)
                        .put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY, String.valueOf(false)))
                .build();
        objectContext = serverRuntime.newContext();
        query = SelectQuery.query(Painting.class);
        Collection<DataNode> dataNodes = ((DataDomain)objectContext.getChannel()).getDataNodes();
        dataNode = dataNodes.iterator().next();
        operationObserver = new FakeOperationObserver();
        connection = dataNode.getDataSource().getConnection();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws SQLException {
        connection.close();
        serverRuntime.shutdown();
    }

    @Benchmark
    public SelectAction createSelectAction() {
        return new SelectAction(query, dataNode);
    }

    @Benchmark
    public void performSelectAction(Blackhole blackhole) throws Exception {
        SelectAction selectAction = new SelectAction(query, dataNode);
        selectAction.performAction(connection, operationObserver);
        blackhole.consume(selectAction);
    }
}
