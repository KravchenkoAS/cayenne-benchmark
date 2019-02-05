package org.apache.cayenne.access.util;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.QueryResponse;

public class FakeQueryResponse implements QueryResponse {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public List<?> currentList() {
        return null;
    }

    @Override
    public int[] currentUpdateCount() {
        return new int[0];
    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public List firstList() {
        return Collections.emptyList();
    }

    @Override
    public int[] firstUpdateCount() {
        return new int[0];
    }
}
