package com.nami.springbootinit.otherttest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nami404
 * * @date 2025/3/6 14:49
 */
public class ThreadLocalExample {
    private static final ThreadLocal<SimpleDateFormat> dataFormatThreadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    public static String formatData(Date date) {
        SimpleDateFormat simpleDateFormat = dataFormatThreadLocal.get();
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread1:" + formatData(new Date()));
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Thread2:" + formatData(new Date()));
        });

        thread1.start();
        thread2.start();
    }
}
