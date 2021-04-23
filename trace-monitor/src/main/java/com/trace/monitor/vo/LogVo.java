package com.trace.monitor.vo;

import com.eson.common.core.util.JsonUtils;
import com.trace.common.domain.IndexLog;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Getter
@Setter
public class LogVo {
    private String appKey;
    private String ip;
    private String traceId;

    private String loggerName;
    private String thread;
    private long logTime;
    private String logLevel;

    private String message;

    public static LogVo of(IndexLog indexLog) {
        return JsonUtils.convertValue(indexLog, LogVo.class);
    }
}
