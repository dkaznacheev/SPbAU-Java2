package ru.spbau.dkaznacheev.myjunit;

import org.junit.Test;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.*;

public class TestExecutorTest {
    public TestExecutor executor;

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
            if (info.name.equals("test1")) {
                assertTrue(info.result.isPassed());
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
            if (info.name.equals("test2")) {
                assertTrue(info.result.isIgnored());
                assertEquals("ignored, reason: Ignore", info.result.toString());
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
            if (info.name.equals("test3")) {
                System.out.println(info.result.toString());
                assertTrue(info.result.isFailed());
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
            if (info.name.equals("test4")) {
                assertTrue(info.result.isPassed());
                checked = true;
            }
        }

        assertTrue(checked);
    }

}