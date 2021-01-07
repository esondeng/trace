package com.trace.core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
@Setter
@Getter
public class Span {
    /**
     * span id, 类似0， 0.1，0.2 用来表示调用层级
     */
    private String id;

    /**
     * root span id
     * 链路第一个rootSpan id是0，第二个JVM的root Span id是1
     */
    private String rootSpanId;
    private String traceId;
    private String name;
    private String serviceType;

    private String clientAppKey;
    private String clientIp;

    private String appKey;
    private String ip;

    /**
     * 毫秒
     */
    private long start;
    private long end;
    private int cost;

    private Span parent;
    private List<Span> children;

    /**
     * 生成下一级span的id
     */
    private AtomicInteger childCounter = new AtomicInteger(0);

    public String nextChildId() {
        return id + "." + childCounter.incrementAndGet();
    }

    /**
     * 设置结束时间
     */
    public void end() {
        this.end = System.currentTimeMillis();
        this.cost = (int) (end - start);
    }

    public boolean isRoot() {
        return rootSpanId.equals(id);
    }


    public void addChild(Span childSpan) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(childSpan);
    }

    @Override
    public String toString() {
        return "id = " + id
                + " name = " + name
                + " traceId = " + traceId
                + " clientAppKey = " + clientAppKey
                + " clientIp = " + clientIp
                + " appKey = " + appKey
                + " ip = " + ip
                + " start = " + start
                + " end = " + end
                + " cost = " + cost;
    }
}
