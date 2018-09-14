package com.github.elo.blcknblck;

public interface ResourceAccessor<T> {

    void updateResource(T newValue);

    T pollResource() throws Exception;
}
