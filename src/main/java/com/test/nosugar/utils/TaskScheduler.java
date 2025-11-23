package com.test.nosugar.utils;

import com.test.nosugar.NoSuger;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = NoSuger.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaskScheduler {
    private static final Queue<Task> queue = new ArrayDeque<>();

    public static void schedule(Runnable action, int delay) {
        queue.add(new Task(action, delay));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        queue.removeIf(task -> {
            task.delay--;
            if (task.delay <= 0) {
                task.action.run();
                return true;
            }
            return false;
        });
    }

    private static class Task {
        Runnable action;
        int delay;

        Task(Runnable action, int delay) {
            this.action = action;
            this.delay = delay;
        }
    }
}