package common;

import java.util.concurrent.*;
import java.util.Objects;

/**
 * Gestionnaire centralise des taches asynchrones du jeu.
 */
public final class AsyncExecutor {
    // Java 21 : threads virtuels pour les taches asynchrones legeres.
    private static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    // Scheduler conserve pour les delais; il declenche ensuite un thread virtuel.
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
     * Lance une tache dans un thread virtuel.
     */
    public static void runAsync(Runnable task) {
        Objects.requireNonNull(task);
        VIRTUAL_EXECUTOR.submit(task);
    }

    /**
     * Remplace un schedule classique : apres delayMs, lance un thread virtuel.
     */
    public static void schedule(Runnable task, long delayMs) {
        SCHEDULER.schedule(() -> runAsync(task), delayMs, TimeUnit.MILLISECONDS);
    }

    public static void shutdown() {
        VIRTUAL_EXECUTOR.shutdown();
        SCHEDULER.shutdown();
    }
}
