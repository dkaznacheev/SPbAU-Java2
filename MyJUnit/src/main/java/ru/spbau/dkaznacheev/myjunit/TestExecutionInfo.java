package ru.spbau.dkaznacheev.myjunit;

public class TestExecutionInfo {

    public final long time;

    public final TestExecutionResult result;

    public final String name;

    public TestExecutionInfo(long time, TestExecutionResult result, String name) {
        this.time = time;
        this.result = result;
        this.name = name;
    }
}
