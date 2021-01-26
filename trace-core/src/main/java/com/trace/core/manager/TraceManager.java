package com.trace.core.manager;

import java.util.Collection;
import java.util.List;

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
                                          String request,
                                          ThrowCallable<T> callable,
                                          List<Runnable> mdcRunnableList) {
        startSpan(consumerContext, serviceType, name, request, mdcRunnableList);
        return invoke(callable, mdcRunnableList);
    }

    /**
     * Web端入口或者内部调用
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          ThrowCallable<T> callable,
                                          List<Runnable> mdcRunnableList) {
        startSpan(serviceType, name, mdcRunnableList);
        return invoke(callable, mdcRunnableList);

    }

    /**
     * 记录sql入口
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          String sql,
                                          ThrowCallable<T> callable,
                                          List<Runnable> mdcRunnableList) {
        startSpan(serviceType, sql, name, mdcRunnableList);
        return invoke(callable, mdcRunnableList);

    }

    private static <T> T invoke(ThrowCallable<T> callable, List<Runnable> mdcRunnableList) {
        try {
            T result = callable.call();
            Span span = TraceContext.peek();
            String serviceType = span.getServiceType();
            if (ServiceType.JDBC.message().equals(serviceType)) {
                String sql = span.getTag(TraceConstants.SQL_TAG_KEY);
                if (result instanceof Integer || result instanceof Long) {
                    sql = sql + "; Total: " + result;

                }
                else if (result instanceof Collection<?>) {
                    Collection<?> collection = (Collection<?>) result;
                    sql = sql + "; Total: " + collection.size();
                }
                span.putTag(TraceConstants.SQL_TAG_KEY, sql);
            }

            return result;
        }
        catch (Throwable e) {
            fillError(e);
            throw buildException(e);
        }
        finally {
            mdcRunnableList.get(1).run();
            TraceManager.endSpan();
        }
    }

    /**
     * Web端入口
     */
    public static void tracing(ServiceType serviceType,
                               String name,
                               ThrowRunnable runnable,
                               List<Runnable> mdcRunnableList) {
        startSpan(serviceType, name, mdcRunnableList);
        invoke(runnable, mdcRunnableList);
    }


    public static void asyncParent(TraceRunnable traceRunnable) {
        TraceContext.push(traceRunnable.getAsyncParent());
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
        TraceContext.push(traceCallable.getAsyncParent());
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
        TraceContext.push(traceSupplier.getAsyncParent());
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

    private static void invoke(ThrowRunnable runnable, List<Runnable> mdcRunnableList) {
        try {
            runnable.run();
        }
        catch (Throwable e) {
            fillError(e);
            throw buildException(e);
        }
        finally {
            mdcRunnableList.get(1).run();
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
        Span currentSpan = TraceContext.peek();
        if (currentSpan != null) {
            currentSpan.fillErrors(e);
        }
    }

    private static void startSpan(ConsumerContext consumerContext,
                                  ServiceType serviceType,
                                  String name,
                                  String request,
                                  List<Runnable> mdcRunnableList) {
        Span span = Span.of(consumerContext, serviceType, name, request);
        TraceContext.push(span);
        mdcRunnableList.get(0).run();
    }

    private static void startSpan(ServiceType serviceType, String name, List<Runnable> mdcRunnableList) {
        Span parentSpan = TraceContext.peek();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;

        }
        Span span = Span.of(parentSpan, serviceType, name, null);
        TraceContext.push(span);
        mdcRunnableList.get(0).run();
    }

    private static void startSpan(ServiceType serviceType, String sql, String name, List<Runnable> mdcRunnableList) {
        Span parentSpan = TraceContext.peek();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;

        }
        Span span = Span.of(parentSpan, serviceType, name, sql);
        TraceContext.push(span);
        mdcRunnableList.get(0).run();
    }

    private static void endSpan() {
        Span span = TraceContext.pop();
        TraceContainer.getInstance().put(span);
    }
}
