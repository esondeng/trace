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
import com.fasterxml.jackson.databind.JsonNode;
import com.trace.common.domain.IndexLog;
import com.trace.monitor.es.EsClient;
import com.trace.monitor.query.LogQuery;
import com.trace.monitor.vo.AggregationVo;
import com.trace.monitor.vo.LogAggregationsVo;
import com.trace.monitor.vo.LogPageVo;
import com.trace.monitor.vo.LogVo;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Component
public class LogManager {
    private static final String LOG_QUERY = ResourceUtils.getResource("/es/logQuery.txt");
    private static final String LOG_AGGS_QUERY = ResourceUtils.getResource("/es/logAggsQuery.txt");

    @Autowired
    private EsClient esClient;

    public LogPageVo<LogVo> getLogPageVoByQuery(LogQuery logQuery) {
        Map<String, String> map = buildParamMap(logQuery);

        String esQuery = ResourceUtils.replace(LOG_QUERY, map);
        String result = esClient.queryLog(esQuery);

        Integer total = JsonUtils.getValue(result, "hits.total.value", Integer.class);
        long cost = JsonUtils.getValue(result, "took", Long.class);

        List<IndexLog> indexLogs = JsonUtils.getValues(result, "hits.hits._source", IndexLog.class);
        List<LogVo> logVos = Funs.map(indexLogs, LogVo::of);

        return LogPageVo.of(logQuery, total, cost, logVos);
    }

    public LogAggregationsVo getLogAggsByQuery(LogQuery logQuery) {
        Map<String, String> map = buildParamMap(logQuery);

        String esQuery = ResourceUtils.replace(LOG_AGGS_QUERY, map);
        String result = esClient.queryLog(esQuery);

        JsonNode dateArrayAggs = JsonUtils.getValue(result, "aggregations.dateArray.buckets", JsonNode.class);
        List<AggregationVo> aggregationVos = Funs.map(
                JsonUtils.convertList(dateArrayAggs, JsonNode.class),
                jsonNode -> {
                    String name = jsonNode.get("key_as_string").asText();
                    if (name.length() == TimeUtils.DATE_TIME.length()) {
                        name = name.substring(name.indexOf(" "));
                    }
                    long count = jsonNode.get("doc_count").asLong();

                    return AggregationVo.of(name, count);
                });
        long max = aggregationVos.stream()
                .mapToLong(AggregationVo::getCount)
                .max()
                .orElse(0L);
        double tickInterval = (double) max / 10;

        return LogAggregationsVo.of(aggregationVos, tickInterval);
    }

    private Map<String, String> buildParamMap(LogQuery logQuery) {
        Map<String, String> map = new HashMap<>(16);

        map.put("condition", logQuery.getCondition());

        Date startDate = TimeUtils.parseAsDate(logQuery.getStartTime(), TimeUtils.DATE_TIME);
        map.put("start", String.valueOf(startDate.getTime()));

        Date endDate = TimeUtils.parseAsDate(logQuery.getEndTime(), TimeUtils.DATE_TIME);
        map.put("end", String.valueOf(endDate.getTime()));

        map.put("interval", logQuery.getInterval());
        map.put("format", logQuery.getFormat());
        map.put("minBounds", logQuery.getMinBounds());
        map.put("maxBounds", logQuery.getMaxBounds());

        map.put("offset", String.valueOf(logQuery.getOffset()));
        map.put("pageSize", String.valueOf(logQuery.getPageSize()));

        return map;
    }


}
