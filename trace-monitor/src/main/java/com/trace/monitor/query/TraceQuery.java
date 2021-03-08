package com.trace.monitor.query;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;

import com.eson.common.core.util.TimeUtils;

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

    public void validate() {
        // 默认只能查询最近一个月的
        if (StringUtils.isBlank(startTime)) {
            ZonedDateTime monthBefore = TimeUtils.now().minusMonths(1L);
            startTime = TimeUtils.formatAsDateTime(monthBefore.toInstant());
        }
    }
}
