package ru.spbau.dkaznacheev.lazy;

/**
 * Lazy is an interface for lazy computations.
 * Constructed with supplier, it is guaranteed that the supplier will be called only once.
 * @param <T> type of the value that Lazy returns
 */
public interface Lazy <T> {
    /**
     * Returns the value.
     * @return value
     */
    T get();
}
