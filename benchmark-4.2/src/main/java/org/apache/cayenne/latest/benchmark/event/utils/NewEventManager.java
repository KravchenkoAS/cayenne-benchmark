package org.apache.cayenne.latest.benchmark.event.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.di.BeforeScopeEnd;
import org.apache.cayenne.event.EventManager;
import org.apache.cayenne.event.EventSubject;

/**
 * @since 4.2
 */
public class NewEventManager implements EventManager {

    private static final int DEFAULT_EXECUTOR_THREADS = 2;

    // every N-th event submission will launch cleanup task, that purge GC-ed listeners
    private static final long CLEANUP_TASK_THRESHOLD = 1000L;

    private final Map<EventSubject, Collection<Listener>> listenersBySubject = new ConcurrentHashMap<>();
    private final Map<String, MethodHandle> methodHandleCache = new ConcurrentHashMap<>();
    private final AtomicLong taskSubmitCounter = new AtomicLong(0L);
    private final ExecutorService executorService;
    private final Runnable refCleanupTask;

    public NewEventManager() {
        this(DEFAULT_EXECUTOR_THREADS);
    }

    public NewEventManager(int executorThreads) {
        executorService = Executors.newFixedThreadPool(executorThreads);
        refCleanupTask = () -> listenersBySubject.values().forEach(
                listeners -> listeners.removeIf(listener -> listener.objectRef.get() == null)
        );
    }

    @Override
    public boolean isSingleThreaded() {
        return false;
    }

    @Override
    public void addListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject) {
        addNonBlockingListener(listener, methodName, eventParameterClass, subject, null);
    }

    @Override
    public void addNonBlockingListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject) {
        addNonBlockingListener(listener, methodName, eventParameterClass, subject, null);
    }

    @Override
    public void addListener(Object listener, String methodName, Class<?> eventParameterClass, EventSubject subject, Object sender) {
        addNonBlockingListener(listener, methodName, eventParameterClass, subject);
    }

    @Override
    public void addNonBlockingListener(Object object, String method, Class<?> eventClass, EventSubject subject, Object sender) {
        Reference<?> objectRef = new WeakReference<>(Objects.requireNonNull(object, "Listener is null"));
        Class<?> listenerClass = object.getClass();
        MethodHandle methodHandle = getMethodHandle(listenerClass, method, eventClass);
        Listener listener = new Listener(objectRef, methodHandle, sender);

        listenersBySubject.computeIfAbsent(subject, subj -> new ConcurrentLinkedQueue<>()).add(listener);
    }

    @Override
    public boolean removeListener(Object listener) {
        boolean removed = false;
        for(Collection<Listener> listeners : listenersBySubject.values()) {
            removed |= listeners.removeIf(next -> next.mappedToObject(listener));
        }
        return removed;
    }

    @Override
    public boolean removeAllListeners(EventSubject subject) {
        Collection<Listener> listeners = listenersBySubject.remove(subject);
        return listeners != null && !listeners.isEmpty();
    }

    @Override
    public boolean removeListener(Object listener, EventSubject subject) {
        return listenersBySubject
                .getOrDefault(subject, Collections.emptyList())
                .removeIf(next -> next.mappedToObject(listener));
    }

    @Override
    public boolean removeListener(Object listener, EventSubject subject, Object sender) {
        return removeListener(listener, subject);
    }

    @Override
    public void postEvent(EventObject event, EventSubject subject) {
        postNonBlockingEvent(event, subject);
    }

    @Override
    public void postNonBlockingEvent(EventObject event, EventSubject subject) {
        executorService.submit(() -> listenersBySubject
                .get(subject)
                .removeIf(listener -> listener.apply(event))
        );
        checkAndSubmitCleanupTask();
    }

    private void checkAndSubmitCleanupTask() {
        long submitted = taskSubmitCounter.incrementAndGet();
        if(submitted % CLEANUP_TASK_THRESHOLD == 0) {
            executorService.submit(refCleanupTask);
        }
    }

    private MethodHandle getMethodHandle(Class<?> listenerClass, String method, Class<?> eventClass) {
        return methodHandleCache.computeIfAbsent(listenerClass.getName() + '/' + method, key -> {
            try {
                Method methodRef = listenerClass.getDeclaredMethod(method, eventClass);
                methodRef.setAccessible(true);
                return MethodHandles.lookup().unreflect(methodRef);
            } catch (NoSuchMethodException | IllegalAccessException ex) {
                throw new CayenneRuntimeException("Unable to find method %s() for %s", method, listenerClass);
            }
        });
    }

    private static class Listener implements Function<EventObject, Boolean> {
        /**
         * Reference to the object that will receive event
         */
        private final Reference<?> objectRef;

        /**
         * Method that processes event
         */
        private final MethodHandle handle;

        /**
         * Desired sender, events will be filtered by it, null means that listener will accept events from all senders
         */
        private final Object sender;

        private Listener(Reference<?> objectRef, MethodHandle handle, Object sender) {
            this.objectRef = objectRef;
            this.handle = handle;
            this.sender = sender;
        }

        /**
         * @param event to process
         * @return true if this listener's reference is null and it should be removed from collection of listeners
         */
        @Override
        public Boolean apply(EventObject event) {
            if(this.sender != null && this.sender != event.getSource()) {
                return false;
            }

            try {
                Object object = objectRef.get();
                if(object != null) {
                    handle.invoke(object, event);
                } else {
                    return true;
                }
            } catch (Throwable ex) {
                // do nothing...
            }
            return false;
        }

        private boolean mappedToObject(Object object) {
            return object == objectRef.get();
        }
    }

    @BeforeScopeEnd
    public void shutdown() {
        executorService.shutdownNow();
    }
}
