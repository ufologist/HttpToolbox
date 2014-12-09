package com.github.ufologist.http.example;

import java.util.Date;

import it.sauronsoftware.cron4j.Scheduler;

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
		// XXX 没有办法在子线程中停止Scheduler
		// 暂时找不到停止的办法, 反正一般定时任务也是没有停止期限的
		System.out.println(new Date());
		System.out.println("task is run");
	    }
	});

	// Starts the scheduler.
	scheduler.start();
	// XXX 只能在主线程中停止Scheduler
	// scheduler.stop();

	System.out.println(new Date());
	System.out.println("Scheduler is start");
    }
}
