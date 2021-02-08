package com.trace.monitor.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/26
 */
@Getter
@Setter
public class TraceQuery {
    private String applicationName;
    private String name;
    private String ip;
    private String traceId;
    private Long minCost;
    private Long maxCost;
    private String exceptionInfo;
    private String startTime;
    private String endTime;
    /**
     * 默认20条记录
     */
    private int resultCount = 20;
}
