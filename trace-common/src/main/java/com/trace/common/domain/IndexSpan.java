package com.trace.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trace.core.Span;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/04
 */
@Getter
@Setter
public class IndexSpan {
    private String traceId;
    private String name;
    private String id;
    private int depth;

    private String clientAppKey;
    private String clientIp;
    private String appKey;
    private String ip;

    private long start;
    private long end;
    private long cost;

    private String serviceType;

    private List<String> errorMessages;

    private Map<String, String> tagMap;

    public static IndexSpan of(Span span) {
        IndexSpan indexSpan = new IndexSpan();
        indexSpan.setTraceId(span.getTraceId());
        indexSpan.setName(span.getName());
        indexSpan.setId(span.getId());
        indexSpan.setDepth(span.getDepth());

        indexSpan.setClientAppKey(span.getClientAppKey());
        indexSpan.setClientIp(span.getClientIp());
        indexSpan.setAppKey(span.getAppKey());
        indexSpan.setIp(span.getIp());

        indexSpan.setStart(span.getStart());
        indexSpan.setEnd(span.getEnd());
        indexSpan.setCost(span.getCost());

        indexSpan.setServiceType(span.getServiceType());

        if (span.getErrorMessages() != null) {
            indexSpan.setErrorMessages(new ArrayList<>(span.getErrorMessages()));
        }

        if (span.getTagMap() != null) {
            indexSpan.setTagMap(new HashMap<>(span.getTagMap()));
        }

        return indexSpan;
    }
}
