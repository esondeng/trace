package com.trace.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/18
 */
@Getter
@Setter
public class IndexLog {
    private String appKey;
    private String ip;
    private String traceId;

    private String loggerName;
    private String thread;
    private long logTime;
    private String logDate;
    private String logLevel;

    private String message;
}
