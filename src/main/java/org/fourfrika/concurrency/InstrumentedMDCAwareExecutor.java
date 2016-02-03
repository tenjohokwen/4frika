package org.fourfrika.concurrency;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.DisposableBean;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class InstrumentedMDCAwareExecutor extends ThreadPoolExecutor implements DisposableBean {

    private final MetricRegistry metricRegistry;
    private final String METRICS_PREFIX;

    public InstrumentedMDCAwareExecutor(int corePoolSize,
                                        int maximumPoolSize,
                                        long keepAliveTimeSecs,
                                        BlockingQueue<Runnable> workQueue,
                                        String poolPrefix,
                                        RejectedExecutionHandler handler,
                                        MetricRegistry metricRegistry) {
        super(corePoolSize, maximumPoolSize, keepAliveTimeSecs, TimeUnit.SECONDS, workQueue, new ThreadFactoryBuilder().setNameFormat(poolPrefix + "-%d").build(), handler);
        this.metricRegistry = metricRegistry;
        this.METRICS_PREFIX = poolPrefix;
        ExecutorInstrumentor.configureMetrics(metricRegistry, METRICS_PREFIX, this);
    }


    @Override
    public void execute(Runnable command) {
        super.execute(instrument(command));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return super.newTaskFor(instrument(runnable), value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return super.newTaskFor(instrument(callable));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(instrument(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(instrument(task), result);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return super.invokeAny(instrument(tasks));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(instrument(task));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return super.invokeAny(instrument(tasks), timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return super.invokeAll(instrument(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return super.invokeAll(instrument(tasks), timeout, unit);
    }

    private Runnable instrument(Runnable runnable) {
        return InstrumentedMDCAwareExecutables.instrument(runnable, metricRegistry, METRICS_PREFIX);
    }

    private <T> Callable<T> instrument(Callable<T> callable) {
        return InstrumentedMDCAwareExecutables.instrument(callable, metricRegistry, METRICS_PREFIX);
    }

    private <T> Collection<? extends Callable<T>> instrument(Collection<? extends Callable<T>> tasks) {
        return InstrumentedMDCAwareExecutables.instrumentTasks(tasks, metricRegistry, METRICS_PREFIX);
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

}
