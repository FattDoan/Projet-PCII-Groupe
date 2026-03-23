package common;

import java.util.concurrent.*;
import java.util.Objects;

/**
 * Gestionnaire centralise des taches asynchrones du jeu.
 */
public final class AsyncExecutor {
    // Java 21 lightweight Virtual Threads for simple async tasks
    private static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    // We still keep a ScheduledExecutor for things that need a specific delay,
    // but we'll use it to "trigger" virtual threads.
    private static final ScheduledExecutorService SCHEDULER = 
        Executors.newScheduledThreadPool(1, namedFactory("pcii-scheduler-"));

    private AsyncExecutor() {}

    private static ThreadFactory namedFactory(String prefix) {
        return runnable -> {
            Thread thread = new Thread(runnable, prefix);
            thread.setDaemon(true);
            return thread;
        };
    }

    /**
     * Runs a task in a lightweight Virtual Thread.
     */
    public static void runAsync(Runnable task) {
        Objects.requireNonNull(task);
        VIRTUAL_EXECUTOR.submit(task);
    }

    /**
     * Replaces your schedule logic. After delayMs, it starts a Virtual Thread.
     */
    public static void schedule(Runnable task, long delayMs) {
        SCHEDULER.schedule(() -> runAsync(task), delayMs, TimeUnit.MILLISECONDS);
    }

    public static void shutdown() {
        VIRTUAL_EXECUTOR.shutdown();
        SCHEDULER.shutdown();
    }
}
