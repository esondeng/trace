package com.trace.monitor.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eson.common.core.util.Funs;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.ResourceUtils;
import com.eson.common.core.util.TimeUtils;
import com.trace.common.domain.IndexLog;
import com.trace.monitor.es.EsClient;
import com.trace.monitor.query.LogQuery;
import com.trace.monitor.vo.LogPageVo;
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

    public LogPageVo<LogVo> getLogVosByQuery(LogQuery logQuery) {
        Map<String, String> map = buildParamMap(logQuery);

        String esQuery = ResourceUtils.replace(LOG_QUERY, map);
        String result = esClient.queryLog(esQuery);

        Integer total = JsonUtils.getValues(result, "hits.total.value", Integer.class).get(0);
        long cost = JsonUtils.getValues(result, "took", Long.class).get(0);

        List<IndexLog> indexLogs = JsonUtils.getValues(result, "hits.hits._source", IndexLog.class);
        List<LogVo> logVos = Funs.map(indexLogs, LogVo::of);

        return LogPageVo.of(logQuery, total, cost, logVos);
    }

    private Map<String, String> buildParamMap(LogQuery logQuery) {
        Map<String, String> map = new HashMap<>(16);

        map.put("condition", logQuery.getCondition());

        Date startDate = TimeUtils.parseAsDate(logQuery.getStartTime(), TimeUtils.DATE_TIME);
        map.put("start", String.valueOf(startDate.getTime()));

        Date endDate = TimeUtils.parseAsDate(logQuery.getEndTime(), TimeUtils.DATE_TIME);
        map.put("end", String.valueOf(endDate.getTime()));

        map.put("offset", String.valueOf(logQuery.getOffset()));
        map.put("pageSize", String.valueOf(logQuery.getPageSize()));

        return map;
    }
}
