package com.trace.monitor.vo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

    private List<String> messages;

    public static LogVo of(IndexLog indexLog) {
        LogVo vo = JsonUtils.convertValue(indexLog, LogVo.class);

        if (StringUtils.isNotBlank(indexLog.getMessage())) {
            vo.setMessages(JsonUtils.parseList(indexLog.getMessage(), String.class));
        }

        return vo;

    }
}
