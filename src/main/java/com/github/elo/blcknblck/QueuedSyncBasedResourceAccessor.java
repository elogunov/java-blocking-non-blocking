package com.github.elo.blcknblck;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Waiting resource accessor without locking and context switching.
 * Internally extends the
 * @see AbstractQueuedSynchronizer
 *
 */
public class QueuedSyncBasedResourceAccessor implements ResourceAccessor<String> {

    private final AtomicReference<String> ref = new AtomicReference<>();
    private final Sync sync = new Sync();
    //NOTE: for testing reasons we just store the updated values by thread in map <thread Id, value>
    private Map<Integer,String> results;

    public QueuedSyncBasedResourceAccessor(Map<Integer, String> map) {
        this.results = map;
    }

    @Override
    public void updateResource(String newValue) {
        //we want to unpark only one thread when setting value
        //for the first time and only and avoid unparking on subsequent
        //updates.
        //That is why getAndSet + check the oldValue == null and only in that case release
        // instead of get and sync.release
        String oldValue = ref.getAndSet(newValue);
        if(oldValue == null) {
            sync.release(0);
        }
    }

    @Override
    public String pollResource() throws Exception {
        int arg = reserveResultsSlot();
        sync.acquireInterruptibly(arg);
        if(ref.get() != null) {
            sync.release(0);
        }
        return releaseResultSlot();
    }

    private String releaseResultSlot() {
        int code = Long.hashCode(Thread.currentThread().getId());
        return results.get(code);
    }

    private int reserveResultsSlot() {
        return Long.hashCode(Thread.currentThread().getId());
    }

    /**
     * @see AbstractQueuedSynchronizer
     * state access
     * queue manipulation
     *
     */
    private class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int arg) {
            while(true) {
                String oldValue = ref.get();
                if (oldValue == null) {
                    return false;
                }
                if (!ref.compareAndSet(oldValue, null)) {
                    continue; //retry CAS
                }
                //hack to get result from the method
                results.put(arg, oldValue);
                return true;
            }
        }

        @Override
        protected boolean tryRelease(int arg) {
            return true; //object is always "released"
        }
    }
}
