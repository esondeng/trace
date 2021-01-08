package com.trace.core;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 传输给服务提供者的上下文
 *
 * @author dengxiaolin
 * @since 2021/01/07
 */
@Getter
@Setter
public class ConsumerContext implements Serializable {
    private String traceId;

    /**
     * 提供者span相当于消费者的childSpan
     * consumerChildId = consumerSpanId + "." + consumerChildIndex
     * consumerSpan相当于parentSpan
     */
    private String consumerChildId;

    private String clientAppKey;
    private String clientIp;

    public static ConsumerContext of(Span span) {
        ConsumerContext consumerContext = new ConsumerContext();
        consumerContext.setTraceId(span.getTraceId());
        consumerContext.setConsumerChildId(span.nextChildId());

        consumerContext.setClientAppKey(span.getAppKey());
        consumerContext.setClientIp(span.getClientIp());

        return consumerContext;
    }
}
