package ru.spbau.dkaznacheev.myjunit;

import ru.spbau.dkaznacheev.myjunit.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class TestExecutor {
    private List<Method> beforeClassMethods;
    private List<Method> beforeMethods;
    private List<Method> afterClassMethods;
    private List<Method> afterMethods;
    private List<Method> testMethods;
    private Class<?> clazz;

    public TestExecutor(Class<?> clazz) {
        beforeClassMethods = new LinkedList<>();
        beforeMethods = new LinkedList<>();
        afterClassMethods = new LinkedList<>();
        afterMethods = new LinkedList<>();
        testMethods = new LinkedList<>();
        this.clazz = clazz;

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeClass.class)) {
                beforeClassMethods.add(method);
            }
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterClass.class)) {
                afterClassMethods.add(method);
            }
            if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }
    }

    public List<TestExecutionInfo> executeTests() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instance = clazz.newInstance();
        List<TestExecutionInfo> results = new LinkedList<>();

        executeAll(instance, beforeClassMethods);

        for (Method testMethod : testMethods) {
            executeAll(instance, beforeMethods);
            TestExecutionInfo result = executeTest(instance, testMethod);
            executeAll(instance, afterMethods);
            results.add(result);
        }

        executeAll(instance, afterClassMethods);

        return results;
    }

    private TestExecutionInfo executeTest(Object instance, Method testMethod) throws IllegalAccessException {
        Test test = testMethod.getAnnotation(Test.class);
        if (test.ignore().equals("")) {
            long startTime = System.currentTimeMillis();
            try {
                testMethod.invoke(instance);
                long endTime = System.currentTimeMillis();
                return new TestExecutionInfo(
                        endTime - startTime,
                        TestExecutionResult.passed(),
                        testMethod.getName()
                );
            } catch (InvocationTargetException e) {
                long endTime = System.currentTimeMillis();
                TestExecutionResult result;
                if (e.getCause().getClass().equals(test.expected())) {
                    result = TestExecutionResult.passed();
                } else {
                    result = TestExecutionResult.failed(e.getCause().toString());
                }
                return new TestExecutionInfo(
                        endTime - startTime,
                        result,
                        testMethod.getName()
                );
            }
        } else {
            return new TestExecutionInfo(
                    0,
                    TestExecutionResult.ignored(test.ignore()),
                    testMethod.getName()
            );
        }
    }

    private void executeAll(Object instance, List<Method> methods) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            method.invoke(instance);
        }
    }

}
