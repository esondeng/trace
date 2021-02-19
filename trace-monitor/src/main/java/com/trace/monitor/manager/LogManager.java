package com.trace.monitor.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eson.common.web.vo.PageVo;
import com.trace.monitor.es.EsClient;
import com.trace.monitor.query.LogQuery;
import com.trace.monitor.vo.LogVo;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Component
public class LogManager {
    @Autowired
    private EsClient esClient;

    public PageVo<LogVo> getLogVosByQuery(LogQuery logQuery) {
        return null;
    }
}
