package ru.spbau.dkaznacheev.myjunit;

public class TestExecutionInfo {

    public long getTime() {
        return time;
    }

    public TestExecutionResult getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    private final long time;

    private final TestExecutionResult result;

    private final String name;

    public TestExecutionInfo(long time, TestExecutionResult result, String name) {
        this.time = time;
        this.result = result;
        this.name = name;
    }
}
