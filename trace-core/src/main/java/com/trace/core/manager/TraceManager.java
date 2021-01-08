package com.trace.core.manager;

import java.util.UUID;

import com.trace.core.ChildContext;
import com.trace.core.Span;
import com.trace.core.TraceCollector;
import com.trace.core.TraceConfig;
import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.utils.NetworkUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
@Slf4j
public class TraceManager {

    public static void startSpan(ChildContext childContext, ServiceType serviceType, String name) {
        Span parentSpan = TraceContext.get();
        if (parentSpan == null) {
            parentSpan = buildRootSpan(childContext, serviceType, name);
            TraceContext.set(parentSpan);
        }
        else {
            Span childSpan = buildChildSpan(parentSpan, name);
            TraceContext.set(childSpan);
        }
    }


    public static void endSpan() {
        Span span = TraceContext.pop();
        TraceCollector.getInstance().collect(span);
    }


    private static Span buildRootSpan(ChildContext childContext, ServiceType serviceType, String name) {
        Span span = new Span();
        String rootSpanId = childContext.getConsumerChildId();
        span.setId(rootSpanId);

        if (TraceConstants.DUMMY_SPAN_ID.equals(rootSpanId)) {
            rootSpanId = TraceConstants.HEAD_SPAN_ID;
            span.setId(rootSpanId);
            span.setTraceId(buildTranceId());
        }
        else {
            span.setClientAppKey(childContext.getClientAppKey());
            span.setClientIp(childContext.getClientIp());
            span.setTraceId(childContext.getTraceId());
        }

        span.setRootSpanId(rootSpanId);
        span.setServiceType(serviceType.message());

        span.setName(name);

        span.setAppKey(TraceConfig.getAppKey());
        span.setIp(NetworkUtils.getLocalIp());
        span.setStart(System.currentTimeMillis());

        return span;
    }

    private static Span buildChildSpan(Span parentSpan, String name) {
        Span span = new Span();
        span.setId(parentSpan.nextChildId());
        span.setTraceId(parentSpan.getTraceId());
        span.setRootSpanId(parentSpan.getRootSpanId());
        span.setName(name);
        span.setStart(System.currentTimeMillis());

        span.setClientAppKey(parentSpan.getClientAppKey());
        span.setClientIp(parentSpan.getClientIp());

        span.setAppKey(parentSpan.getAppKey());
        span.setIp(parentSpan.getIp());

        span.setParent(parentSpan);
        parentSpan.addChild(span);

        return span;
    }


    private static String buildTranceId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
