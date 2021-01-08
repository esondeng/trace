package com.trace.core.manager;

import java.util.UUID;

import com.eson.common.core.constants.Constants;
import com.eson.common.core.utils.IpUtils;
import com.trace.core.ConsumerContext;
import com.trace.core.Span;
import com.trace.core.TraceCollector;
import com.trace.core.TraceConfig;
import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.function.ThrowRunnable;
import com.trace.core.function.ThrowSupplier;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
@Slf4j
public class TraceManager {

    /**
     * 微服务提供者入口
     */
    public static <T> T tracingWithReturn(ConsumerContext consumerContext,
                                          ServiceType serviceType,
                                          String name,
                                          ThrowSupplier<T> supplier) {
        TraceManager.startSpan(consumerContext, serviceType, name);
        return invoke(supplier);
    }

    /**
     * Web端入口
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          ThrowSupplier<T> supplier) {
        TraceManager.startSpan(serviceType, name);
        return invoke(supplier);

    }

    private static <T> T invoke(ThrowSupplier<T> supplier) {
        try {
            return supplier.get();
        }
        catch (Throwable e) {
            fillError(e);
            throw new RuntimeException(e);
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
        TraceManager.startSpan(consumerContext, serviceType, name);
        invoke(runnable);
    }

    /**
     * Web端入口
     */
    public static void tracing(ServiceType serviceType,
                               String name,
                               ThrowRunnable runnable) {
        TraceManager.startSpan(serviceType, name);
        invoke(runnable);
    }

    private static void invoke(ThrowRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable e) {
            fillError(e);
            throw new RuntimeException(e);
        }
        finally {
            TraceManager.endSpan();
        }
    }

    private static void fillError(Throwable e) {
        Span currentSpan = TraceContext.get();
        if (currentSpan != null) {
            currentSpan.fillErrors(e);
        }
    }

    private static void startSpan(ConsumerContext consumerContext, ServiceType serviceType, String name) {
        Span span = buildSpan(consumerContext, serviceType, name);
        TraceContext.set(span);
    }

    private static void startSpan(ServiceType serviceType, String name) {
        Span parentSpan = TraceContext.get();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;
        }
        Span span = buildSpan(parentSpan, serviceType, name);
        TraceContext.set(span);
    }


    private static void endSpan() {
        Span span = TraceContext.pop();
        TraceCollector.getInstance().collect(span);
    }


    /**
     * 微服务提供者场景使用
     */
    private static Span buildSpan(ConsumerContext consumerContext, ServiceType serviceType, String name) {
        Span span = new Span();
        span.setName(name);
        span.setServiceType(serviceType.message());

        String rootSpanId = consumerContext.getConsumerChildId();
        fillIdInfo(span, rootSpanId);

        span.setClientAppKey(consumerContext.getClientAppKey());
        span.setClientIp(consumerContext.getClientIp());
        span.setTraceId(consumerContext.getTraceId());

        fillServerInfo(span);
        return span;
    }


    /**
     * 内部调用使用parentSpan可能是null，标书第一个span
     */
    private static Span buildSpan(Span parentSpan, ServiceType serviceType, String name) {
        Span span = new Span();
        span.setName(name);

        if (parentSpan == TraceConstants.DUMMY_SPAN) {
            span.setServiceType(serviceType.message());
            span.setTraceId(buildTranceId());

            fillIdInfo(span, TraceConstants.HEAD_SPAN_ID);
            fillServerInfo(span);

            return span;
        }
        else {
            fillIdInfo(span, parentSpan.nextChildId());
            span.setTraceId(parentSpan.getTraceId());
            span.setStart(System.currentTimeMillis());
            span.setServiceType(ServiceType.INNER_CALL.message());

            copyFromParent(parentSpan, span);

            span.setParent(parentSpan);
            parentSpan.addChild(span);
            return span;
        }
    }


    private static void fillIdInfo(Span span, String id) {
        span.setId(id);
        String[] strs = id.split(Constants.POINT_SPLITTER);
        span.setDepth(strs.length);
        span.setOrder(Integer.valueOf(strs[strs.length - 1]));
    }

    private static void fillServerInfo(Span span) {
        span.setAppKey(TraceConfig.getAppKey());
        span.setIp(IpUtils.getLocalIp());
        span.setStart(System.currentTimeMillis());
    }

    private static void copyFromParent(Span parentSpan, Span span) {
        span.setAppKey(parentSpan.getAppKey());
        span.setIp(parentSpan.getIp());
        span.setClientIp(parentSpan.getClientIp());
        span.setClientAppKey(parentSpan.getClientAppKey());
    }


    private static String buildTranceId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
