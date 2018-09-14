package com.github.elo.blcknblck;


/**
 * Simple synchronized implementation
 *
 * Assume we only want to retrieve resource value if it is not null.
 * Otherwise we want to wait the value to become not null.
 */
public class SynchronizedResourceAccessor implements ResourceAccessor<String> {

    private String value;

    public synchronized void updateResource(String newValue) {
        this.value = newValue;
        notifyAll();
    }

    public synchronized String pollResource() throws Exception {
        //standard pattern
        while (value == null) {
            wait();
        }
        String oldValue = value;
        value = null;
        return oldValue;
    }
}
