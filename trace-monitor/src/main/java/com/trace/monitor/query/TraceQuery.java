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
    private String interfaceName;
    private String startTime;
    private String endTime;
    private String durationTime;
}
