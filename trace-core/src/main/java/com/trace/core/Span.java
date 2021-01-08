package com.trace.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;

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
     * 方便排序，比如id是0.1， depth是2，order就是1
     */
    private int depth;

    private int order;

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
    private List<String> errorMessages;


    /**
     * 是否被收集过
     */
    private volatile boolean collected;

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

    public void addChild(Span childSpan) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(childSpan);
    }

    public void fillErrors(Throwable exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        List<String> errorStack = new ArrayList<>();

        if (stackTraceElements != null && stackTraceElements.length > 0) {
            // 最多关心前3个
            int maxErrorNum = Math.min(stackTraceElements.length, 3);
            for (int i = 0; i < maxErrorNum; i++) {
                errorStack.add(stackTraceElements[i].toString());
            }
        }

        errorMessages = errorStack;
    }

    @Override
    public String toString() {
        String content = "id = " + id
                + " name = " + name
                + " traceId = " + traceId
                + " clientAppKey = " + clientAppKey
                + " clientIp = " + clientIp
                + " appKey = " + appKey
                + " ip = " + ip
                + " start = " + start
                + " end = " + end
                + " cost = " + cost;

        if (CollectionUtils.isNotEmpty(errorMessages)) {
            return content + " error = " + String.join(";", errorMessages);
        }
        else {
            return content;
        }


    }
}
