package com.trace.monitor.vo;

import com.eson.common.core.util.JsonUtils;
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
    private long start;
    private long cost;
    private String status;

    public static TraceVo of(IndexSpan indexSpan) {
        return JsonUtils.convertValue(indexSpan, TraceVo.class);
    }
}
