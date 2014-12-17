package com.github.ufologist.http.example;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.Date;

/**
 * 定时任务
 * 
 * @author Sun
 * @version SchedulerExample.java 2014-12-3 下午3:20:12
 */
public class SchedulerExample {
    public static void main(String[] args) {
        testScheduler();
    }

    private static void testScheduler() {
        // Creates a Scheduler instance.
        Scheduler scheduler = new Scheduler();

        // Schedule a task.
        // * * * * *
        // 分 时 日 月 星
        // http://www.sauronsoftware.it/projects/cron4j/manual.php#p02
        scheduler.schedule("4 11 * * *", new Runnable() {
            public void run() {
                System.out.println(new Date());
                System.out.println("task is run");
                // XXX 在子线程中停止Scheduler无法结束JVM
                // 只能通过System.exit来退出JVM
                // scheduler.stop();
                // System.exit(0);
            }
        });

        // Starts the scheduler.
        scheduler.start();
        // XXX 在主线程中停止Scheduler会结束JVM
        // scheduler.stop();

        System.out.println(new Date());
        System.out.println("Scheduler is start");
    }
}
