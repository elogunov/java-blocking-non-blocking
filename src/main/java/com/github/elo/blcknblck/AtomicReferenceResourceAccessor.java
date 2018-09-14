package com.github.elo.blcknblck;

import java.util.concurrent.atomic.AtomicReference;


/**
 * This implementation is non-blocking but
 * does not give us the ability to wait the value if it is null
 */
public class AtomicReferenceResourceAccessor implements ResourceAccessor<String> {

    private final AtomicReference<String> ref = new AtomicReference<>();

    @Override
    public void updateResource(String newValue) {
        ref.set(newValue);
    }

    @Override
    public String pollResource() {
        //cast loop
        while (true) {
            String oldValue = ref.get();
            if(oldValue == null) return null;
            if(ref.compareAndSet(oldValue, null)) {
                return oldValue;
            }
        }
    }
}
