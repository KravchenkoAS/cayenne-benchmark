package org.apache.cayenne.benchmark.event.utils;

import java.util.List;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.ResultIterator;
import org.apache.cayenne.access.OperationObserver;
import org.apache.cayenne.query.Query;

public class FakeOperationObserver implements OperationObserver {
    @Override
    public void nextCount(Query query, int resultCount) {

    }

    @Override
    public void nextBatchCount(Query query, int[] resultCount) {

    }

    @Override
    public void nextRows(Query query, List<?> dataRows) {

    }

    @Override
    public void nextRows(Query q, ResultIterator<?> it) {

    }

    @Override
    public void nextGeneratedRows(Query query, ResultIterator<?> keys, ObjectId idToUpdate) {

    }

    @Override
    public void nextQueryException(Query query, Exception ex) {

    }

    @Override
    public void nextGlobalException(Exception ex) {

    }

    @Override
    public boolean isIteratedResult() {
        return false;
    }
}
