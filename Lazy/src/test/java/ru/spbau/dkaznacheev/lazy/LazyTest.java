package ru.spbau.dkaznacheev.lazy;

import org.junit.Test;

import javax.jws.Oneway;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

public class LazyTest {
    private static final int THREADS_NUM = 1000;

    private static class Job implements Supplier<Boolean> {
        private int called = 0;
        private boolean isLong;

        public Job(boolean isLong) {
            this.isLong = isLong;
        }

        public boolean getShort() {
            called++;
            return true;
        }

        public boolean getLong() {
            called++;
            for (int i = 0; i < 1000000; i++);
            return true;
        }

        public boolean calledOnce() {
            return called == 1;
        }

        @Override
        public Boolean get() {
            if (!isLong)
                return getShort();
            else
                return getLong();
        }
    }

    @Test
    public void singleThreadLazyTest() {
        Job job = new Job(false);
        Lazy<Boolean> lazy = LazyFactory.createSingleThreadLazy(job);
        for (int i = 0; i < 100; i++) {
            lazy.get();
        }
        assertTrue(job.calledOnce());
    }

    @Test
    public void singleThreadLazyLongTest() {
        Job job = new Job(true);
        Lazy<Boolean> lazy = LazyFactory.createSingleThreadLazy(job);
        for (int i = 0; i < 100; i++) {
            lazy.get();
        }
        assertTrue(job.calledOnce());
    }

    @Test
    public void multiThreadLazyShortTest() throws InterruptedException {
        Object object = new Object();
        Supplier<Object> supplier = () -> object;
        Lazy<Object> lazy = LazyFactory.createMultiThreadLazy(supplier);
        List<Thread> threads = new ArrayList<>();
        Runnable runnable = () -> {
            assertTrue(object == lazy.get());
        };

        for (int i = 0; i < THREADS_NUM; i++) {
            threads.add(new Thread(runnable));
        }
        for (int i = 0; i < THREADS_NUM; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < THREADS_NUM; i++) {
            threads.get(i).join();
        }
    }

    @Test
    public void multiThreadLazySameObjectTest() throws InterruptedException {
        Job job = new Job(false);
        Lazy<Boolean> lazy = LazyFactory.createMultiThreadLazy(job);
        List<Thread> threads = new ArrayList<>();
        Runnable runnable = lazy::get;

        for (int i = 0; i < THREADS_NUM; i++) {
            threads.add(new Thread(runnable));
        }
        for (int i = 0; i < THREADS_NUM; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < THREADS_NUM; i++) {
            threads.get(i).join();
        }
        assertTrue(job.calledOnce());
    }

    @Test
    public void multiThreadLazyLongTest() throws InterruptedException{
        Job job = new Job(true);
        Lazy<Boolean> lazy = LazyFactory.createMultiThreadLazy(job);
        List<Thread> threads = new ArrayList<>();
        Runnable runnable = lazy::get;

        for (int i = 0; i < THREADS_NUM; i++) {
            threads.add(new Thread(runnable));
        }
        for (int i = 0; i < THREADS_NUM; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < THREADS_NUM; i++) {
            threads.get(i).join();
        }
        assertTrue(job.calledOnce());
    }
}
