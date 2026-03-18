package common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gestionnaire centralise des taches asynchrones du jeu.
 */
public final class AsyncExecutor {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private AsyncExecutor() {
    }

    public static void runAsync(Runnable task) {
        EXECUTOR.execute(task);
    }

    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }
}