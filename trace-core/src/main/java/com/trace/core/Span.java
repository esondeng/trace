package com.trace.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.util.NetworkUtils;

/**
 * 鉴于agent要使用，这里尽量使用jdk自带的方法，避免引入其他的类
 *
 * @author dengxiaolin
 * @since 2021/01/06
 */
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
    private String sql;
    private String request;
    private String response;
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
     * 新开线程池的时候，copy当前的span放在子线程的threadLocal里面
     */
    private boolean isAsyncParent;

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

    public static Span copyAsAsyncParent(Span span) {
        if (span == null) {
            return null;
        }

        Span copy = new Span();
        copy.setAsyncParent(true);

        // 新先线程直接使用父线程的id
        fillIdInfo(copy, span.getId());
        copyFromParent(copy, span);
        copy.setChildCounter(span.getChildCounter());

        return copy;
    }

    /**
     * 微服务提供者场景使用
     */
    public static Span of(ConsumerContext consumerContext, ServiceType serviceType, String name, String request) {
        Span span = new Span();
        span.setName(name);
        span.setRequest(request);
        span.setServiceType(serviceType.message());

        String rootSpanId = consumerContext.getConsumerChildId();
        fillIdInfo(span, rootSpanId);

        span.setTraceId(consumerContext.getTraceId());
        span.setClientAppKey(consumerContext.getClientAppKey());
        span.setClientIp(consumerContext.getClientIp());

        fillServerInfo(span);
        return span;
    }


    /**
     * 内部调用使用parentSpan可能是null，标书第一个span
     */
    public static Span of(Span parentSpan, ServiceType serviceType, String name, String sql) {
        Span span = new Span();
        span.setName(name);
        span.setSql(sql);

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
            span.setServiceType(serviceType.message());

            copyFromParent(span, parentSpan);

            span.setParent(parentSpan);
            parentSpan.addChild(span);
            return span;
        }
    }


    private static void fillIdInfo(Span span, String id) {
        span.setId(id);
        String[] strs = id.split("\\.");
        span.setDepth(strs.length);
        span.setOrder(Integer.valueOf(strs[strs.length - 1]));
    }

    private static void fillServerInfo(Span span) {
        span.setAppKey(TraceConfig.getAppKey());
        span.setIp(NetworkUtils.getLocalIp());
        span.setStart(System.currentTimeMillis());
    }

    private static void copyFromParent(Span span, Span parentSpan) {
        span.setTraceId(parentSpan.getTraceId());
        span.setAppKey(parentSpan.getAppKey());
        span.setIp(parentSpan.getIp());
        span.setClientIp(parentSpan.getClientIp());
        span.setClientAppKey(parentSpan.getClientAppKey());
    }

    private static String buildTranceId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    @Override
    public String toString() {
        String content = "id = " + id
                + " serviceType = " + serviceType
                + " depth = " + depth
                + " order = " + order
                + " name = " + name
                + " sql = " + sql
                + " traceId = " + traceId
                + " clientAppKey = " + clientAppKey
                + " clientIp = " + clientIp
                + " appKey = " + appKey
                + " ip = " + ip
                + " start = " + start
                + " end = " + end
                + " cost = " + cost;

        if (errorMessages != null && !errorMessages.isEmpty()) {
            return content + " error = " + String.join(";", errorMessages);
        }
        else {
            return content;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getClientAppKey() {
        return clientAppKey;
    }

    public void setClientAppKey(String clientAppKey) {
        this.clientAppKey = clientAppKey;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Span getParent() {
        return parent;
    }

    public void setParent(Span parent) {
        this.parent = parent;
    }

    public List<Span> getChildren() {
        return children;
    }

    public void setChildren(List<Span> children) {
        this.children = children;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public boolean isAsyncParent() {
        return isAsyncParent;
    }

    public void setAsyncParent(boolean asyncParent) {
        isAsyncParent = asyncParent;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public AtomicInteger getChildCounter() {
        return childCounter;
    }

    public void setChildCounter(AtomicInteger childCounter) {
        this.childCounter = childCounter;
    }
}
