package ru.spbau.dkaznacheev.myjunit;


public class TestExecutionResult {

    private final String failMessage;

    private final String ignoreMessage;

    private TestExecutionResult(String failMessage, String ignoreMessage) {
        this.failMessage = failMessage;
        this.ignoreMessage = ignoreMessage;
    }

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
        return new TestExecutionResult(null, null);
    }

    public static TestExecutionResult ignored(String ignoreMessage) {
        return new TestExecutionResult(null, ignoreMessage);
    }

    public static TestExecutionResult failed(String failMessage) {
        return new TestExecutionResult(failMessage, null);
    }
}
