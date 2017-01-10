package com.securityx.modelfeature;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class FeatureServiceThreadPool implements Managed{

    private final Logger LOGGER = LoggerFactory.getLogger(FeatureServiceThreadPool.class);
    private ScheduledExecutorService executor;

    public FeatureServiceThreadPool(int numberOfThreads) {
        executor = Executors.newScheduledThreadPool(numberOfThreads);
    }

    public Future<?> addTask(Runnable runnable){
        return executor.submit(runnable);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeunit){
         return executor.scheduleAtFixedRate(runnable, initialDelay, period, timeunit);
    }

    public ScheduledFuture<?> scheduleNow(Runnable runnable){
        return executor.schedule(runnable, 0L, TimeUnit.MILLISECONDS);
    }

    public boolean cancelTask(Future<?> future){
        return future.cancel(true);
    }

    public void shutdown(){
        if (executor == null)
            return;

        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.debug("Cancelling non-finished tasks");
            }
            executor.shutdownNow();
            LOGGER.debug("Shutdown finished");
        }
    }

    @Override
    public void start() throws Exception {
            ///no-op
    }

    @Override
    public void stop() throws Exception {
        LOGGER.debug("Shutting down api-thread pool");
        shutdown();
    }
}
