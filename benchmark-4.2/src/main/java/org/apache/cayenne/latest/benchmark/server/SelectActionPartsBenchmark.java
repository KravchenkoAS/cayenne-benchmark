package org.apache.cayenne.latest.benchmark.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ResultIterator;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.jdbc.JDBCResultIterator;
import org.apache.cayenne.access.jdbc.RowDescriptor;
import org.apache.cayenne.access.jdbc.RowDescriptorBuilder;
import org.apache.cayenne.access.jdbc.reader.RowReader;
import org.apache.cayenne.access.translator.select.SelectTranslator;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.log.JdbcEventLogger;
import org.apache.cayenne.log.NoopJdbcEventLogger;
import org.apache.cayenne.query.QueryMetadata;
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
import persistent.Painting;

@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 6, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SelectActionPartsBenchmark {

    private ServerRuntime serverRuntime;
    private ObjectContext objectContext;
    static SelectQuery<Painting> query;
    static SelectTranslator selectTranslator;
    static DataNode dataNode;

    @Setup(Level.Iteration)
    public void setUp() {
        serverRuntime = ServerRuntime.builder()
                .addConfig("cayenne-project.xml")
                .addModule(binder -> binder.bind(JdbcEventLogger.class).to(NoopJdbcEventLogger.class))
                .build();
        objectContext = serverRuntime.newContext();
        query = SelectQuery.query(Painting.class);
        dataNode = ((DataDomain)objectContext.getChannel()).getDataNodes().iterator().next();
        selectTranslator = dataNode.selectTranslator(query);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        serverRuntime.shutdown();
    }

    @State(Scope.Benchmark)
    public static class DescriptorSetup {

        RowDescriptor rowDescriptor;
        QueryMetadata queryMetadata;

        @Setup(Level.Iteration)
        public void setUp() throws SQLException {
            rowDescriptor = new RowDescriptorBuilder().setColumns(selectTranslator.getResultColumns()).getDescriptor(
                    dataNode.getAdapter().getExtendedTypes());
            queryMetadata = query.getMetaData(dataNode.getEntityResolver());
        }

        @TearDown(Level.Iteration)
        public void teadDown() {

        }
    }

    @State(Scope.Benchmark)
    public static class ConnectionSetup {

        Connection connection;
        String sql;

        @Setup(Level.Iteration)
        public void setUp() throws Exception {
            connection = dataNode.getDataSource().getConnection();
            SelectTranslator translator = dataNode.selectTranslator(query);
            sql = translator.getSql();
        }

        @TearDown(Level.Iteration)
        public void tearDown() throws SQLException {
            connection.close();
        }
    }

    @State(Scope.Benchmark)
    public static  class IteratorSetup {

        PreparedStatement statement;
        Connection connection;
        ResultSet rs;
        RowReader<?> rowReader;

        @Setup(Level.Iteration)
        public void setUp() throws Exception {
            connection = dataNode.getDataSource().getConnection();
            String sql = selectTranslator.getSql();
            statement = connection.prepareStatement(sql);
            try {
                rs = statement.executeQuery();
            } catch (Exception ex) {
                statement.close();
                throw ex;
            }
            RowDescriptor rowDescriptor = new RowDescriptorBuilder().setColumns(selectTranslator.getResultColumns()).getDescriptor(
                    dataNode.getAdapter().getExtendedTypes());
            QueryMetadata queryMetadata = query.getMetaData(dataNode.getEntityResolver());
            rowReader = dataNode.rowReader(rowDescriptor, queryMetadata, selectTranslator.getAttributeOverrides());
        }

        @TearDown(Level.Iteration)
        public void tearDown() throws SQLException {
            connection.close();
        }
    }

    @Benchmark
    public String getSql() throws Exception {
        SelectTranslator translator = dataNode.selectTranslator(query);
        return translator.getSql();
    }

    @Benchmark
    public ResultSet buildResultSet(ConnectionSetup connectionSetup) throws SQLException {
        PreparedStatement statement = connectionSetup.connection.prepareStatement(connectionSetup.sql);
        ResultSet rs;
        try {
            rs = statement.executeQuery();
        } catch (Exception ex) {
            statement.close();
            throw ex;
        }
        return rs;
    }

    @Benchmark
    public RowDescriptor rowDescriptorCreation() throws SQLException {
        return new RowDescriptorBuilder().setColumns(selectTranslator.getResultColumns()).getDescriptor(
                dataNode.getAdapter().getExtendedTypes());
    }

    @Benchmark
    public RowReader<?> rowReaderCreation(DescriptorSetup descriptor) {
        return dataNode.rowReader(descriptor.rowDescriptor, descriptor.queryMetadata, selectTranslator.getAttributeOverrides());
    }

    @Benchmark
    public List<?> buildResultIterator(IteratorSetup iteratorSetup) {
        ResultIterator<?> it = new JDBCResultIterator<>(iteratorSetup.statement, iteratorSetup.rs, iteratorSetup.rowReader);
        List<?> resultRows;
        try {
            resultRows = it.allRows();
        } finally {
            it.close();
        }
        return resultRows;
    }
}
