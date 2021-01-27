package com.trace.monitor.vo;

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

    public static TraceVo of(String name, String traceId, String start, long cost, String status) {
        TraceVo vo = new TraceVo();
        vo.setName(name);
        vo.setTraceId(traceId);
        vo.setStart(start);
        vo.setCost(cost);
        vo.setStatus(status);

        return vo;
    }
}
