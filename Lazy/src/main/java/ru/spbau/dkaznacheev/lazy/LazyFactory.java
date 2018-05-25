package ru.spbau.dkaznacheev.lazy;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Class for creating Lazy instances.
 */
public class LazyFactory {
    /**
     * Creates non-thread-safe Lazy instance.
     * @param supplier supplier for Lazy
     * @param <T> type of value
     * @return a new instance of Lazy
     */
    public static <T> Lazy<T> createSingleThreadLazy(@NotNull Supplier<T> supplier) {
        return new Lazy<T>() {
            private Supplier<T> lazySupplier = supplier;
            private T result;
            @Override
            public T get() {
                if (lazySupplier != null) {
                    result = lazySupplier.get();
                    lazySupplier = null;
                }
                return result;
            }
        };
    }

    /**
     * Creates thread-safe Lazy instance.
     * @param supplier supplier for Lazy
     * @param <T> type of value
     * @return a new instance of Lazy
     */
    public static <T> Lazy<T> createMultiThreadLazy(@NotNull Supplier<T> supplier) {
        return new LazyImpl<T>(supplier);
    }
}
