package ru.spbau.dkaznacheev.threadpool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class ThreadPoolTest {
    @Test
    public void threadPoolSimpleTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        LightFuture<Integer> future = pool.addTask(() -> 4);
        assertEquals(4, (int)future.get());
        assertTrue(future.isReady());
    }

    @Test
    public void threadPoolManyTaskTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        for (int i = 0; i < 100; i++) {
            LightFuture<Integer> future = pool.addTask(() -> 4);
            assertEquals(4, (int) future.get());
        }
    }

    @Test
    public void threadPoolSimpleLongTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        LightFuture<Integer> future = pool.addTask(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            return 4;
        });
        assertFalse(future.isReady());
        assertEquals(4, (int)future.get());
        assertTrue(future.isReady());
    }

    @Test (expected = LightFutureException.class)
    public void threadPoolThrowsTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        pool.addTask(() -> {
            throw new RuntimeException();
        });
    }

    @Test
    public void threadPoolThrowsDoesntBreak() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        for (int i = 0; i < 10; i++) {
            pool.addTask(() -> {
                throw new RuntimeException();
            });
        }
        LightFuture<Integer> future = pool.addTask(() -> 4);
        assertEquals(4, (int)future.get());
    }

    @Test
    public void threadPoolShutdownTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        pool.addTask(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
            throw new RuntimeException();
        });
        pool.shutdown();
    }

    @Test
    public void threadPoolThenApplyTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        LightFuture<Integer> future1 = pool.addTask(() -> 4);
        LightFuture<Integer> future2 = future1.thenApply((x) -> x * 2);
        assertEquals(8, (int)future2.get());
    }

    @Test
    public void threadPoolThenApplyLongTest() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(4);
        LightFuture<Integer> future1 = pool.addTask(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            return 4;
        });
        LightFuture<Integer> future2 = future1.thenApply((x) -> x * 2);
        assertEquals(8, (int)future2.get());
    }

    private class Counter {
        private int value = 0;

        private int getValue() {
            return value;
        }

        private void increment() {
            value++;
        }
    }

    @Test
    public void threadPoolHasNThreads() throws Exception {
        ThreadPool pool = new ThreadPoolImpl(5);
        Counter counter = new Counter();
        for (int i = 0; i < 100; i++) {
            pool.addTask(() -> {
                synchronized (counter) {
                    counter.increment();
                }
                while (!Thread.interrupted()){
                }
                return true;
            });
        }
        Thread.sleep(100);
        pool.shutdown();
        assertEquals(5, counter.getValue());
    }
}