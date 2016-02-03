package org.fourfrika.concurrency;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.DisposableBean;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class InstrumentedMDCAwareScheduledExecutor extends ScheduledThreadPoolExecutor implements DisposableBean {

    private final MetricRegistry metricRegistry;
    private final String METRICS_PREFIX;

    public InstrumentedMDCAwareScheduledExecutor(int corePoolSize, String poolPrefix, RejectedExecutionHandler handler, MetricRegistry metricRegistry) {
        super(corePoolSize, new ThreadFactoryBuilder().setNameFormat(poolPrefix + "-%d").build(), handler);
        this.metricRegistry = metricRegistry;
        this.METRICS_PREFIX = poolPrefix;
        ExecutorInstrumentor.configureMetrics(metricRegistry, METRICS_PREFIX, this);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        return super.decorateTask(instrument(runnable), task);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        return super.decorateTask(instrument(callable), task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(instrument(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return super.schedule(instrument(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(instrument(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(instrument(command), initialDelay, delay, unit);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(instrument(command));
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
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(instrument(task));
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
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return super.invokeAny(instrument(tasks));
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
