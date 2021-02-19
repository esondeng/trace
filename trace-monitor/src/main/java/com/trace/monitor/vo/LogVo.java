package com.trace.monitor.vo;

import java.util.List;

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

    private List<String> messages;
}
