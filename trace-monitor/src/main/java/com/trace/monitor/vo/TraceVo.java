package com.trace.monitor.vo;

import java.util.Date;

import com.eson.common.core.util.TimeUtils;
import com.trace.common.domain.IndexSpan;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/27
 */
@Getter
@Setter
public class TraceVo {
    private String name;
    private String traceId;
    private String start;
    private long cost;
    private String status;

    public static TraceVo of(IndexSpan indexSpan) {
        TraceVo vo = new TraceVo();
        vo.setName(indexSpan.getName());
        vo.setTraceId(indexSpan.getTraceId());
        vo.setStart(TimeUtils.formatAsDateTime(new Date(indexSpan.getStart())));
        vo.setCost(indexSpan.getCost());
        vo.setStatus(indexSpan.getStatus());

        return vo;
    }
}
