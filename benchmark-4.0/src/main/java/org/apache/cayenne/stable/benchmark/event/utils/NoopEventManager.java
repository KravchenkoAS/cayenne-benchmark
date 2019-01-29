package org.apache.cayenne.stable.benchmark.event.utils;

import java.util.EventObject;

import org.apache.cayenne.event.EventManager;
import org.apache.cayenne.event.EventSubject;

public class NoopEventManager implements EventManager {
    @Override
    public boolean isSingleThreaded() {
        return false;
    }

    @Override
    public void addListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject) {

    }

    @Override
    public void addNonBlockingListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject) {

    }

    @Override
    public void addListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject, Object sender) {

    }

    @Override
    public void addNonBlockingListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject, Object sender) {

    }

    @Override
    public boolean removeListener(Object listener) {
        return false;
    }

    @Override
    public boolean removeAllListeners(EventSubject subject) {
        return false;
    }

    @Override
    public boolean removeListener(Object listener, EventSubject subject) {
        return false;
    }

    @Override
    public boolean removeListener(Object listener, EventSubject subject, Object sender) {
        return false;
    }

    @Override
    public void postEvent(EventObject event, EventSubject subject) {

    }

    @Override
    public void postNonBlockingEvent(EventObject event, EventSubject subject) {

    }
}
