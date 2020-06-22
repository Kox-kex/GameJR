package com.javarush.task.task32.task3209;

public class ExceptionHandler {

    public static void log(Exception e) {
        e.printStackTrace();
        System.out.println(e.toString());
    }
}
