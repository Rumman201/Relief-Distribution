package com.example.reliefdistribution.data.concurrency;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized, bounded thread pool manager for the Relief Distribution System.
 * Eliminates unbounded thread proliferation and main-thread I/O stalls.
 */
public class AppExecutors {

    private static volatile AppExecutors sInstance;

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r, prefix + "-" + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private AppExecutors() {
        // Disk I/O: Single-thread bounded queue for serializing cache and preference writes
        this.diskIO = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(500),
                new NamedThreadFactory("relief-disk"),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );

        // Network I/O: Bounded thread pool (Core: 2, Max: 4) for background network and telemetry calls
        this.networkIO = new ThreadPoolExecutor(
                2, 4, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                new NamedThreadFactory("relief-net"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // Main UI Thread Executor
        this.mainThread = new MainThreadExecutor();
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (AppExecutors.class) {
                if (sInstance == null) {
                    sInstance = new AppExecutors();
                }
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }
}
