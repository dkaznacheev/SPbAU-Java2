package ru.spbau.dkaznacheev.myjunit;


public class TestExecutionResult {
    private String failMessage;
    private String ignoreMessage;

    @Override
    public String toString() {
        if (ignoreMessage != null) {
            return "ignored, reason: " + ignoreMessage;
        }
        if (failMessage != null) {
            return "failed, exception: " + failMessage;
        }
        return "passed";
    }

    public boolean isPassed() {
        return failMessage == null && ignoreMessage == null;
    }

    public boolean isFailed() {
        return failMessage != null;
    }

    public boolean isIgnored() {
        return ignoreMessage != null;
    }

    public static TestExecutionResult passed() {
        return new TestExecutionResult();
    }

    public static TestExecutionResult ignored(String ignoreMessage) {
        TestExecutionResult result = new TestExecutionResult();
        result.ignoreMessage = ignoreMessage;
        return result;
    }

    public static TestExecutionResult failed(String failMessage) {
        TestExecutionResult result = new TestExecutionResult();
        result.failMessage = failMessage;
        return result;
    }
}
