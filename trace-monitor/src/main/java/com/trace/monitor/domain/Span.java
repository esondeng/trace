package com.trace.monitor.domain;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/27
 */
@Getter
@Setter
public class Span {
    private String traceId;
    private String name;
    private String id;
    private int depth;

    private String appKey;
    private String ip;

    private long start;
    private long end;
    private long cost;

    private String serviceType;

    private List<String> errorMessages;

    private Map<String, String> tagMap;
}
