package ru.spbau.dkaznacheev.myjunit;

import org.junit.Test;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.*;

public class TestExecutorTest {
    private TestExecutor executor;

    @Before
    public void init() {
        executor = new TestExecutor(TestClass.class);
    }

    @Test
    public void simpleTest() throws Exception {
        List<TestExecutionInfo> results = executor.executeTests();
        assertEquals(4, results.size());

        boolean checked = false;
        for (TestExecutionInfo info : results) {
            if (info.getName().equals("test1")) {
                assertTrue(info.getResult().isPassed());
                checked = true;
            }
        }

        assertTrue(checked);
    }

    @Test
    public void ignoredParameterWorks() throws Exception {
        List<TestExecutionInfo> results = executor.executeTests();

        boolean checked = false;
        for (TestExecutionInfo info : results) {
            if (info.getName().equals("test2")) {
                assertTrue(info.getResult().isIgnored());
                assertEquals("ignored, reason: Ignore", info.getResult().toString());
                checked = true;
            }
        }

        assertTrue(checked);
    }

    @Test
    public void unexpectedExceptionThrows() throws Exception {
        List<TestExecutionInfo> results = executor.executeTests();

        boolean checked = false;
        for (TestExecutionInfo info : results) {
            if (info.getName().equals("test3")) {
                System.out.println(info.getResult().toString());
                assertTrue(info.getResult().isFailed());
                checked = true;
            }
        }

        assertTrue(checked);
    }

    @Test
    public void expectedParameterWorks() throws Exception {
        List<TestExecutionInfo> results = executor.executeTests();
        boolean checked = false;
        for (TestExecutionInfo info : results) {
            if (info.getName().equals("test4")) {
                assertTrue(info.getResult().isPassed());
                checked = true;
            }
        }

        assertTrue(checked);
    }

}