package com.NaAlOH4;

import com.NaAlOH4.tgapi.TelegramAPIException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskQueue extends Thread {
    // private final Object lock = new Object();
    private List<Task> tasks = new ArrayList<>();

    void execute(@NotNull Runnable toRun) {

        execute(toRun, 0);
    }

    void execute(@NotNull Runnable toRun, long ms) {
        if (ms < 0) throw new WTFException();
        final Task task = new Task(System.nanoTime() + ms*1000000, toRun);
        synchronized (tasks) {
            for (int i = 0; i <= tasks.size(); i++) {
                if (i == tasks.size()) {
                    tasks.add(i, task);
                    break;
                } else if (tasks.get(i).time < ms) { // From the earliest to the oldest.
                    tasks.add(i, task);
                    break;
                }
            }
        }

        synchronized (this) {
            try {
                this.notify();
                Main.printDebug("notified");
            } catch (IllegalMonitorStateException e) {
                Main.printDebug("notify failed");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        while (true) {
            Main.printDebug("checking tasks");
            boolean empty;
            synchronized (tasks) {
                empty = tasks.isEmpty();
            }
            Main.printDebug("tasks.isEmpty(): "+ empty);
            if (empty) {
                synchronized (this) {
                    try {
                        Main.printDebug("waiting for task...");
                        this.wait();
                        Main.printDebug("waiting for task - notified");
                        continue;
                    } catch (InterruptedException ignore) {
                        throw new WTFException();
                    }
                }
            }

            Task task;
            synchronized (tasks) {
                if(tasks.isEmpty()){
                    System.err.println("tasks list still is empty");
                }
                task = tasks.get(0); // The earliest one
            }
            long currentTime = System.nanoTime();
            if (task.time <= currentTime) {
                Main.printDebug("task is already should be exec");
                // exec task
                try {
                    Main.printDebug("exec task...");
                    task.toRun.run();
                    Main.printDebug("exec task - done");
                } catch (IOException | TelegramAPIException e) {
                    e.printStackTrace();
                }
                synchronized (tasks) { // Enter critical section
                    Main.printDebug("removing old task...");
                    tasks.remove(0);
                }
            } else {
                try {
                    Main.printDebug("task shouldn't be exec, wait: ");
                    synchronized (this) {
                        this.wait((task.time - currentTime) / 1000000);
                    }
                    Main.printDebug("wait canceled");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Task {
        long time;
        Runnable toRun;

        public Task(long time, Runnable toRun) {
            this.time = time;
            this.toRun = toRun;
        }
    }
}