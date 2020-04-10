package com.nowcoder.community;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Ttt implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " " + "run...");
    }

    public static void main(String[] args) {
//        Ttt t = new Ttt();
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
//        System.out.println("execute");
//        scheduledExecutorService.scheduleAtFixedRate(t, 10000, 1000, TimeUnit.MILLISECONDS);
//        System.out.println("done");
        if (!(0 == 0) | true) {
            System.out.println(true);
        }
        System.out.println(1 % 5);
    }
}
