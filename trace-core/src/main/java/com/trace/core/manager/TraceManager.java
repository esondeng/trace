package com.trace.core.manager;

import com.eson.common.function.ThrowCallable;
import com.eson.common.function.ThrowRunnable;
import com.trace.core.ConsumerContext;
import com.trace.core.Span;
import com.trace.core.TraceContainer;
import com.trace.core.TraceContext;
import com.trace.core.async.TraceCallable;
import com.trace.core.async.TraceRunnable;
import com.trace.core.async.TraceSupplier;
import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
public class TraceManager {

    /**
     * 微服务提供者入口
     */
    public static <T> T tracingWithReturn(ConsumerContext consumerContext,
                                          ServiceType serviceType,
                                          String name,
                                          ThrowCallable<T> callable) {
        startSpan(consumerContext, serviceType, name);
        return invoke(callable);
    }

    /**
     * Web端入口或者内部调用
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          ThrowCallable<T> callable) {
        startSpan(serviceType, name);
        return invoke(callable);

    }

    /**
     * 记录sql入口
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          String sql,
                                          ThrowCallable<T> callable) {
        startSpan(serviceType, sql, name);
        return invoke(callable);

    }

    private static <T> T invoke(ThrowCallable<T> callable) {
        try {
            return callable.call();
        }
        catch (Throwable e) {
            fillError(e);
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
        }
    }

    /**
     * 微服务提供者入口
     */
    public static void tracing(ConsumerContext consumerContext,
                               ServiceType serviceType,
                               String name,
                               ThrowRunnable runnable) {
        startSpan(consumerContext, serviceType, name);
        invoke(runnable);
    }

    /**
     * Web端入口
     */
    public static void tracing(ServiceType serviceType,
                               String name,
                               ThrowRunnable runnable) {
        startSpan(serviceType, name);
        invoke(runnable);
    }


    public static void asyncParent(TraceRunnable traceRunnable) {
        TraceContext.set(traceRunnable.getAsyncParent());
        try {
            traceRunnable.getRunnable().run();
        }
        catch (Throwable e) {
            throw buildException(e);
        }
        finally {
            // 异步的parent已经收集过，直接放弃
            TraceContext.pop();
        }
    }


    public static <V> V asyncParent(TraceCallable<V> traceCallable) {
        TraceContext.set(traceCallable.getAsyncParent());
        try {
            return traceCallable.getCallable().call();
        }
        catch (Throwable e) {
            throw buildException(e);
        }
        finally {
            // 异步的parent已经收集过，直接放弃
            TraceContext.pop();
        }
    }

    public static <T> T asyncParent(TraceSupplier<T> traceSupplier) {
        TraceContext.set(traceSupplier.getAsyncParent());
        try {
            return traceSupplier.getSupplier().get();
        }
        catch (Throwable e) {
            throw buildException(e);
        }
        finally {
            // 异步的parent已经收集过，直接放弃
            TraceContext.pop();
        }
    }

    private static void invoke(ThrowRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable e) {
            fillError(e);
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
        }
    }

    private static RuntimeException buildException(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        else {
            return new RuntimeException(e);
        }
    }

    private static void fillError(Throwable e) {
        Span currentSpan = TraceContext.get();
        if (currentSpan != null) {
            currentSpan.fillErrors(e);
        }
    }

    private static void startSpan(ConsumerContext consumerContext, ServiceType serviceType, String name) {
        Span span = Span.of(consumerContext, serviceType, name);
        TraceContext.set(span);
    }

    private static void startSpan(ServiceType serviceType, String name) {
        Span parentSpan = TraceContext.get();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;

        }
        Span span = Span.of(parentSpan, serviceType, name, null);
        TraceContext.set(span);
    }

    private static void startSpan(ServiceType serviceType, String sql, String name) {
        Span parentSpan = TraceContext.get();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;

        }
        Span span = Span.of(parentSpan, serviceType, name, sql);
        span.setSql(sql);
        TraceContext.set(span);
    }

    private static void endSpan() {
        Span span = TraceContext.pop();
        TraceContainer.getInstance().put(span);
    }
}
