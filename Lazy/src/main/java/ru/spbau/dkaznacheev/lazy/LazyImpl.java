package ru.spbau.dkaznacheev.lazy;

import java.util.function.Supplier;

/**
 * A thread-safe implementation of Lazy interface, only runs its Supplier once.
 * @param <T> type that Lazy returns
 */
public class LazyImpl <T> implements Lazy<T> {

    /**
     * A stub object that resembles the uninitialized value.
     */
    private static final Object notInitialized = new Object();

    /**
     * Value that Lazy returns.
     */
    private volatile T value;

    /**
     * The supplier that generates the value of Lazy.
     */
    private final Supplier<T> supplier;

    @SuppressWarnings("unchecked")
    public LazyImpl(Supplier<T> supplier) {
        value = (T)notInitialized;
        this.supplier = supplier;
    }

    /**
     * Returns the supplier's value.
     * If it was already called, returns the remembered value, not running the supplier again.
     * @return supplier's value
     */
    @Override
    public T get() {
        if (value == notInitialized) {
            synchronized (supplier) {
                if (value == notInitialized) {
                    value = supplier.get();
                }
            }
        }
        return value;
    }
}
