package ru.spbau.dkaznacheev.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An implementation of ThreadPool based on queue of jobs that are executed by workers.
 */
public class ThreadPoolImpl implements ThreadPool {

    /**
     * A queue of jobs.
     */
    private final Queue<LightFutureImpl<?>> queue = new LinkedList<>();

    /**
     * The workers of the pool.
     * ThreadPool is created with a fixed number of workers.
     */
    private final Thread[] workers;

    /**
     * An implementation of LightFuture interface for
     * @param <T>
     */
    private class LightFutureImpl<T> implements LightFuture<T> {

        /**
         * The LightFuture's supplier.
         */
        private final Supplier<T> supplier;

        /**
         * The status of the computation.
         */
        private volatile boolean isReady = false;

        /**
         * The result of the computation.
         */
        private volatile T result;

        /**
         * Whether an exception occurred during supplier's execution.
         */
        private volatile boolean caughtException = false;

        private LightFutureImpl(@NotNull Supplier<T> supplier) {
            this.supplier = supplier;
        }

        /**
         * Returns whether a computation is completed.
         * @return whether a computation is completed
         */
        @Override
        public boolean isReady() {
            return isReady;
        }

        /**
         * Returns the result of the computation, if it is not completed, this method blocks the execution.
         * @return result of the computation
         * @throws LightFutureException if an exception occurred during a computation
         */
        @Override
        public T get() throws LightFutureException {
            synchronized (this) {
                while (!isReady) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new LightFutureException();
                    }
                }
            }
            if (caughtException) {
                throw new LightFutureException();
            }
            return result;
        }

        /**
         * Computes the value and tells everybody that the value is ready.
         */
        private void compute() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                caughtException = true;
            }
            isReady = true;
            synchronized (this) {
                notifyAll();
            }
        }

        /**
         * Applies the result of current computation to another and returns another computation's LightFuture.
         * @param function the function to compute with this LightFuture's result
         * @param <U> type that the function returns
         * @return the result of the function
         */
        @Override
        public <U> LightFuture<U> thenApply(Function<T, U> function) {
            return addTask(() -> function.apply(get()));
        }
    }

    public ThreadPoolImpl(int poolSize) {
        workers = new Thread[poolSize];

        Runnable workerRunnable = () -> {
            LightFutureImpl<?> future;
            try {
                while (!Thread.interrupted()) {
                    synchronized (queue) {
                        while (queue.isEmpty()) {
                            queue.wait();
                        }
                        future = queue.poll();
                    }
                    future.compute();
                }
            } catch (InterruptedException e) {
            }
        };

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Thread(workerRunnable);
            workers[i].start();
        }
    }

    /**
     * Adds a task to the pool, returning a LightFuture object.
     * @param supplier supplier for the computation
     * @param <T> type of the result
     * @return LightFuture object that will contain the result of computation
     */
    @Override
    public <T> LightFuture<T> addTask(Supplier<T> supplier) {
        LightFutureImpl<T> future = new LightFutureImpl<T>(supplier);
        synchronized (queue) {
            queue.add(future);
            queue.notify();
        }
        return future;
    }

    /**
     * Shuts down all of its current workers.
     */
    @Override
    public void shutdown() {
        for (Thread thread: workers) {
            thread.interrupt();
        }
    }
}
