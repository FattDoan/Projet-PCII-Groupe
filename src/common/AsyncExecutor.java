package common;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/**
 * Gestionnaire centralise des taches asynchrones du jeu.
 */
public final class AsyncExecutor {
    private static final Logger LOGGER = Logger.getLogger(AsyncExecutor.class.getName());

    private static final int SHORT_CORE_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
    private static final int SHORT_MAX_THREADS = Math.max(8, Runtime.getRuntime().availableProcessors() * 4);
    private static final int SHORT_QUEUE_CAPACITY = 512;

    private static final int LONG_CORE_THREADS = 0;
    private static final int LONG_MAX_THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());

    private static final int KEEP_ALIVE_SECONDS = 30;

    // Compteur pour produire des noms de threads lisibles.
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(1);

    // Télémétrie courte durée.
    private static final AtomicLong SHORT_SUBMITTED = new AtomicLong(0);
    private static final AtomicLong SHORT_REJECTED = new AtomicLong(0);

    // Télémétrie longue durée.
    private static final AtomicLong LONG_SUBMITTED = new AtomicLong(0);
    private static final AtomicLong LONG_REJECTED = new AtomicLong(0);
    private static final AtomicLong LONG_FALLBACK_STARTED = new AtomicLong(0);
    private static final AtomicLong LONG_FALLBACK_FAILED = new AtomicLong(0);

    // Pool dédié aux tâches courtes (ex: déplacement minerai).
    private static final ThreadPoolExecutor SHORT_TASK_EXECUTOR;
    // Pool dédié aux tâches longues (ex: foreuses en boucle).
    private static final ThreadPoolExecutor LONG_TASK_EXECUTOR;

    static {
        ThreadPoolExecutor shortPool = new ThreadPoolExecutor(
            SHORT_CORE_THREADS,
            SHORT_MAX_THREADS,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(SHORT_QUEUE_CAPACITY),
            namedFactory("pcii-short-")
        );
        shortPool.allowCoreThreadTimeOut(true);
        SHORT_TASK_EXECUTOR = shortPool;

        ThreadPoolExecutor longPool = new ThreadPoolExecutor(
            LONG_CORE_THREADS,
            LONG_MAX_THREADS,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            namedFactory("pcii-long-")
        );
        longPool.allowCoreThreadTimeOut(true);
        LONG_TASK_EXECUTOR = longPool;
    }

    private AsyncExecutor() {
    }

    private static ThreadFactory namedFactory(String prefix) {
        return runnable -> {
            Thread thread = new Thread(runnable, prefix + THREAD_COUNTER.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
    }

    private static void runFallbackDaemon(Runnable task, String prefix) {
        Thread fallbackThread = new Thread(() -> {
            try {
                task.run();
            } catch (RuntimeException ex) {
                LONG_FALLBACK_FAILED.incrementAndGet();
                throw ex;
            }
        }, prefix + THREAD_COUNTER.getAndIncrement());
        fallbackThread.setDaemon(true);
        LONG_FALLBACK_STARTED.incrementAndGet();
        fallbackThread.start();
    }

    public static void runAsync(Runnable task) {
        // Validation explicite pour éviter un NPE plus loin dans l'executor.
        Objects.requireNonNull(task, "task=null");
        SHORT_SUBMITTED.incrementAndGet();

        // Garde défensive: si l'application est en extinction, on ne soumet pas.
        if (!SHORT_TASK_EXECUTOR.isShutdown()) {
            try {
                SHORT_TASK_EXECUTOR.execute(task);
            } catch (RejectedExecutionException ignored) {
                SHORT_REJECTED.incrementAndGet();
                // Le pool peut être en phase de fermeture entre le check et le submit.
                LOGGER.fine(() -> "Soumission refusee (short executor en fermeture): " + task.getClass().getName());
            }
        }
    }

    /**
     * Soumet une tâche longue durée qui ne doit jamais bloquer le thread appelant.
     */
    public static void runLongLived(Runnable task) {
        Objects.requireNonNull(task, "task=null");
        LONG_SUBMITTED.incrementAndGet();

        if (!LONG_TASK_EXECUTOR.isShutdown()) {
            try {
                LONG_TASK_EXECUTOR.execute(task);
            } catch (RejectedExecutionException ex) {
                LONG_REJECTED.incrementAndGet();
                LOGGER.log(Level.WARNING, "Saturation long executor, lancement fallback daemon pour: " + task.getClass().getName(), ex);
                runFallbackDaemon(task, "pcii-long-fallback-");
            }
        }
    }

    /**
     * Snapshot compact de la charge et des compteurs d'exécution.
     */
    public static String telemetrySnapshot() {
        long shortSubmitted = SHORT_SUBMITTED.get();
        long shortRejected = SHORT_REJECTED.get();
        long longSubmitted = LONG_SUBMITTED.get();
        long longRejected = LONG_REJECTED.get();
        long longFallbackStarted = LONG_FALLBACK_STARTED.get();
        long longFallbackFailed = LONG_FALLBACK_FAILED.get();

        return "AsyncExecutor{" +
            "short{submitted=" + shortSubmitted +
            ", rejected=" + shortRejected +
            ", active=" + SHORT_TASK_EXECUTOR.getActiveCount() +
            ", poolSize=" + SHORT_TASK_EXECUTOR.getPoolSize() +
            ", queue=" + SHORT_TASK_EXECUTOR.getQueue().size() +
            "}, long{submitted=" + longSubmitted +
            ", rejected=" + longRejected +
            ", fallbackStarted=" + longFallbackStarted +
            ", fallbackFailed=" + longFallbackFailed +
            ", active=" + LONG_TASK_EXECUTOR.getActiveCount() +
            ", poolSize=" + LONG_TASK_EXECUTOR.getPoolSize() +
            "}}";
    }

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR =
        Executors.newScheduledThreadPool(SHORT_CORE_THREADS, namedFactory("pcii-scheduler-"));

    public static void schedule(Runnable task, long delayMs) {
        if (!SCHEDULED_EXECUTOR.isShutdown()) {
            SCHEDULED_EXECUTOR.schedule(task, delayMs, TimeUnit.MILLISECONDS);
        }
    }
    /**
     * Log utilitaire prêt à l'emploi pour diagnostiquer la charge en live.
     */
    public static void logTelemetry() {
        LOGGER.info(AsyncExecutor::telemetrySnapshot);
    }

    public static void shutdown() {
        // Interrompt toutes les tâches pour un arrêt rapide de l'application.
        SHORT_TASK_EXECUTOR.shutdownNow();
        LONG_TASK_EXECUTOR.shutdownNow();
    }
}
