package ru.spbau.dkaznacheev.threadpool;

import java.util.function.Supplier;

/**
 * ThreadPool interface. It is capable of adding tasks to it and shutting down all of current workers.
 */
public interface ThreadPool {
    /**
     * Adds a task to the pool, returning a LightFuture object.
     * @param supplier supplier for the computation
     * @param <T> type of the result
     * @return LightFuture object that will contain the result of computation
     */
    <T> LightFuture<T> addTask(Supplier<T> supplier);

    /**
     * Shuts down all of its current workers.
     */
    void shutdown() throws InterruptedException;
}
