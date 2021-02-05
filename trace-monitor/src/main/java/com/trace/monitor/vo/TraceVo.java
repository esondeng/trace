package com.trace.monitor.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.eson.common.core.util.TimeUtils;
import com.trace.common.domain.IndexSpan;
import com.trace.monitor.enums.SpanStatus;

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
        vo.setTraceId(indexSpan.getTraceId());
        vo.setStart(TimeUtils.formatAsDateTime(new Date(indexSpan.getStart())));
        vo.setCost(indexSpan.getCost());
        vo.setStatus(StringUtils.isBlank(indexSpan.getErrorMessage()) ? SpanStatus.SUCCESS.message() : SpanStatus.FAILED.message());

        return vo;
    }
}
