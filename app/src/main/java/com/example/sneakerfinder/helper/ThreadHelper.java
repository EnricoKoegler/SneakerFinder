package com.example.sneakerfinder.helper;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.core.os.HandlerCompat;

/**
 * Implements a {@link ThreadPoolExecutor} for background processing.
 * Mainly used by {@link com.example.sneakerfinder.repo.ShoeRepository} to retrieve data from
 * the {@link com.example.sneakerfinder.db.AppDb} or the {@link com.example.sneakerfinder.network.RestClient}.
 */
public class ThreadHelper {
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 4;
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private static final int KEEP_ALIVE_TIME = 60;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue
    );

    private static final Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static Handler getHandler() {
        return handler;
    }
}