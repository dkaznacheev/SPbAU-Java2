package ru.spbau.dkaznacheev.myjunit.annotations;

import ru.spbau.dkaznacheev.myjunit.NoException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

    String ignore() default "";

    Class<? extends Exception> expected() default NoException.class;
}
