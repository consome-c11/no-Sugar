package com.test.nosugar.utils;

import com.test.nosugar.NoSugar;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaskScheduler {

    private static final Queue<Task> queue = new ArrayDeque<>();
    private static final Object lock = new Object();

    public static void schedule(Runnable action, int delay) {
        if (action == null) {
            throw new IllegalArgumentException("Task action cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay cannot be negative: " + delay);
        }

        synchronized (lock) {
            queue.add(new Task(action, delay));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;

        synchronized (lock) {
            queue.removeIf(task -> {
                if (task == null) return true;
                if (task.action == null) return true;

                task.delay--;
                if (task.delay <= 0) {
                    try {
                        task.action.run();
                    } catch (Exception ex) {
                        NoSugar.LOGGER.error("[NoSugar] Task execution failed: " + ex.getMessage(), ex);
                    }
                    return true;
                }
                return false;
            });
        }
    }

    private static class Task {
        final Runnable action;
        int delay;

        Task(Runnable action, int delay) {
            this.action = action;
            this.delay = delay;
        }

        @Override
        public String toString() {
            return "Task{delay=" + delay + ", action=" + action.getClass().getSimpleName() + "}";
        }
    }
}