package ru.spbau.dkaznacheev.myjunit;

import ru.spbau.dkaznacheev.myjunit.annotations.After;
import ru.spbau.dkaznacheev.myjunit.annotations.AfterClass;
import ru.spbau.dkaznacheev.myjunit.annotations.Before;
import ru.spbau.dkaznacheev.myjunit.annotations.BeforeClass;

public class TestClass {
    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @BeforeClass
    public void beforeClass() {
    }

    @AfterClass
    public void afterClass() {
    }

    @ru.spbau.dkaznacheev.myjunit.annotations.Test
    public void test1() {
    }

    @ru.spbau.dkaznacheev.myjunit.annotations.Test (ignore = "Ignore")
    public void test2() {
    }

    @ru.spbau.dkaznacheev.myjunit.annotations.Test
    public void test3() throws Exception {
        throw new TestException();
    }

    @ru.spbau.dkaznacheev.myjunit.annotations.Test (expected = TestException.class)
    public void test4() throws Exception {
        throw new TestException();
    }
}
