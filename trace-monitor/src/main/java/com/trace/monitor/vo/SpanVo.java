package com.trace.monitor.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.eson.common.core.util.JsonUtils;
import com.trace.monitor.domain.Span;
import com.trace.monitor.enums.SpanStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/27
 */
@Getter
@Setter
public class SpanVo {
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

    private Map<String, String> tagMap;
    private List<String> errorMessages;

    private String status;

    private List<String> childIds;

    private int left;
    private int width;


    public static SpanVo of(Span span, Span rootSpan) {
        SpanVo vo = JsonUtils.convertValue(span, SpanVo.class);
        vo.setStatus(CollectionUtils.isEmpty(vo.getErrorMessages()) ? SpanStatus.SUCCESS.message() : SpanStatus.FAILED.message());

        if (rootSpan != null) {
            int left = (int) ((span.getStart() - rootSpan.getStart()) * 94 / rootSpan.getCost());
            vo.setLeft(left);

            int width = (int) ((span.getCost()) * 94 / rootSpan.getCost());
            vo.setWidth(width);
        }
        else {
            vo.setLeft(0);
            vo.setWidth(94);
        }

        return vo;
    }

    public void addChildId(String childId) {
        if (CollectionUtils.isEmpty(childIds)) {
            childIds = new ArrayList<>();
        }
        childIds.add(childId);
    }
}
