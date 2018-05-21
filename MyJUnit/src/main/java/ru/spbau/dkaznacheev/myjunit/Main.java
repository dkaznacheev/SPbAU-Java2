package ru.spbau.dkaznacheev.myjunit;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Wrong argument format!");
            return;
        }
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file" + args[0])});
            File folder = new File(args[0]);
            if (!folder.isDirectory()) {
                System.err.println("Not a directory!");
                return;
            }
            for (File file : folder.listFiles()) {
                Class<?> clazz = loader.loadClass(file.getName());
                TestExecutor executor = new TestExecutor(clazz);
                List<TestExecutionInfo> infos = executor.executeTests();
                for (TestExecutionInfo info : infos) {
                    printInfo(info);
                }
            }
        }  catch (MalformedURLException e) {
            System.err.println("Invalid path!");
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load classes!");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void printInfo(TestExecutionInfo info) {
        System.out.println(
                "Test "
                + info.name
                + ": "
                + info.result.toString()
                + ", time: "
                + info.time + "ms"
        );
    }
}
