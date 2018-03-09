package ru.spbau.dkaznacheev.threadpool;

import java.util.function.Function;

public interface LightFuture<T> {

    /**
     * Returns whether a computation is completed.
     * @return whether a computation is completed
     */
    boolean isReady();

    /**
     * Returns the result of the computation, if it is not completed, this method blocks the execution.
     * @return result of the computation
     * @throws LightFutureException if an exception occurred during a computation
     */
    T get() throws LightFutureException;

    /**
     * Applies the result of current computation to another and returns another computation's LightFuture.
     * @param function the function to compute with this LightFuture's result
     * @param <U> type that the function returns
     * @return the result of the function
     */
    <U> LightFuture<U> thenApply(Function<T, U> function);
}
