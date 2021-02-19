package com.trace.monitor.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eson.common.core.util.Funs;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.ResourceUtils;
import com.eson.common.web.vo.PageVo;
import com.trace.common.domain.IndexLog;
import com.trace.monitor.es.EsClient;
import com.trace.monitor.query.LogQuery;
import com.trace.monitor.vo.LogVo;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Component
public class LogManager {
    private static final String LOG_QUERY = ResourceUtils.getResource("/es/logQuery.txt");

    @Autowired
    private EsClient esClient;

    public PageVo<LogVo> getLogVosByQuery(LogQuery logQuery) {
        Map<String, String> map = new HashMap<>(16);
        map.put("condition", logQuery.getCondition());
        map.put("offset", String.valueOf(logQuery.getOffset()));
        map.put("pageSize", String.valueOf(logQuery.getPageSize()));

        String esQuery = ResourceUtils.replace(LOG_QUERY, map);
        String result = esClient.queryLog(esQuery);

        Integer total = JsonUtils.getValues(result, "hits.total.value", Integer.class).get(0);

        List<IndexLog> indexLogs = JsonUtils.getValues(result, "hits.hits._source", IndexLog.class);
        List<LogVo> logVos = Funs.map(indexLogs, LogVo::of);

        return PageVo.of(logQuery, total, logVos);
    }
}
