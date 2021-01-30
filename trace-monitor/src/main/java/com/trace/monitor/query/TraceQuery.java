package com.trace.monitor.query;

import com.eson.common.web.query.PageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/26
 */
@Getter
@Setter
public class TraceQuery extends PageQuery {
    private String applicationName;
    private String name;
    private String ip;
    private Long minCost;
    private Long maxCost;
    private String exceptionInfo;
    private String startTime;
    private String endTime;
}
