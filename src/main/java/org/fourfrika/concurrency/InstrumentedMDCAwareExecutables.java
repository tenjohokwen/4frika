package org.fourfrika.concurrency;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.fourfrika.commons.MDCWrapper;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.toList;
import static com.codahale.metrics.MetricRegistry.name;


/**
 */
public class InstrumentedMDCAwareExecutables {

    public static <T> Callable<T> instrument(Callable<T> callable, MetricRegistry registry, String metricPrefix) {
        final ClientContext clientContext = ClientContext.instance();
        final Timer.Context context = registry.timer(MetricRegistry.name(metricPrefix, ExecutorInstrumentor.QUEUE_DELAY_POSTFIX)).time();
        Map<String, String> currentThreadCxt = MDC.getCopyOfContextMap();
        return () -> {
            try {
                context.stop();
                clientContext.setClientMDCContext();
                return callable.call();
            } catch (Exception e) {
                ExecutorExceptionHandler.handleException(e, clientContext, registry.meter(name(metricPrefix, ExecutorInstrumentor.EXCEPTION_POSTFIX)));
                return null;
            } finally {
                MDCWrapper.setContext(currentThreadCxt);
            }
        };
    }

    public static Runnable instrument(Runnable task, MetricRegistry registry, String metricPrefix) {
        ClientContext clientContext = ClientContext.instance();
        final Timer.Context context = registry.timer(MetricRegistry.name(metricPrefix, ExecutorInstrumentor.QUEUE_DELAY_POSTFIX)).time();
        Map<String, String> currentThreadCxt = MDC.getCopyOfContextMap();
        return () -> {
            try {
                context.stop();
                clientContext.setClientMDCContext();
                task.run();
            } catch (Exception e) {
                ExecutorExceptionHandler.handleException(e, clientContext, registry.meter(name(metricPrefix, ExecutorInstrumentor.EXCEPTION_POSTFIX)));
            } finally {
                MDCWrapper.setContext(currentThreadCxt);
            }
        };
    }

    public static <T> Collection<? extends Callable<T>> instrumentTasks(Collection<? extends Callable<T>> tasks, MetricRegistry registry, String metricPrefix) {
        return tasks.stream().map(task -> instrument(task, registry, metricPrefix)).collect(toList());
    }



}
