package com.trace.monitor.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eson.common.core.util.Funs;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.ResourceUtils;
import com.eson.common.core.util.TimeUtils;
import com.eson.common.web.vo.PageVo;
import com.trace.common.domain.IndexSpan;
import com.trace.core.constants.TraceConstants;
import com.trace.monitor.es.EsClient;
import com.trace.monitor.query.TraceQuery;
import com.trace.monitor.vo.TraceDetailVo;
import com.trace.monitor.vo.TraceVo;

/**
 * @author dengxiaolin
 * @since 2021/01/27
 */
@Component
public class TraceManager {
    private static final String TRACE_QUERY = ResourceUtils.getResource("/es/traceQuery.txt");
    private static final String TRACE_DETAIL_QUERY = ResourceUtils.getResource("/es/traceDetailQuery.txt");

    private static final String TERM_CLAUSE = "{\"term\":{\"${name}\":\"${value}\"}}";
    private static final String GTE_RANGE_CLAUSE = "{\"range\":{\"${name}\":{\"gte\":\"${value}\"}}}";
    private static final String LTE_RANGE_CLAUSE = "{\"range\":{\"${name}\":{\"lte\":\"${value}\"}}}";

    @Autowired
    private EsClient esClient;

    public PageVo<TraceVo> getPageVoByQuery(TraceQuery traceQuery) {
        String query = buildEsQuery(traceQuery);
        String result = esClient.query(query);
        List<IndexSpan> indexSpans = JsonUtils.getValues(result, "hits.hits._source", IndexSpan.class);
        int total = JsonUtils.getValues(result, "hits.total.value", Integer.class).get(0);

        return PageVo.of(traceQuery, total, Funs.map(indexSpans, TraceVo::of));
    }

    private String buildEsQuery(TraceQuery traceQuery) {
        String conditions = buildConditions(traceQuery);

        Map<String, String> map = new HashMap<>(16);
        map.put("conditions", conditions);
        map.put("offset", String.valueOf(traceQuery.getOffset()));
        map.put("pageSize", String.valueOf(traceQuery.getPageSize()));

        return ResourceUtils.replace(TRACE_QUERY, map);
    }

    private String buildConditions(TraceQuery traceQuery) {
        List<String> clauseList = new ArrayList<>();
        clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "appKey", "value", traceQuery.getApplicationName()));
        clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "id", "value", TraceConstants.ROOT_SPAN_ID));

        if (StringUtils.isNotBlank(traceQuery.getName())) {
            clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "name", "value", traceQuery.getName()));
        }

        if (StringUtils.isNotBlank(traceQuery.getIp())) {
            clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "ip", "value", traceQuery.getIp()));
        }

        if (traceQuery.getMinCost() != null && traceQuery.getMinCost() > 0L) {
            clauseList.add(ResourceUtils.replace(GTE_RANGE_CLAUSE, "name", "minCost", "value", traceQuery.getMinCost().toString()));
        }

        if (traceQuery.getMaxCost() != null && traceQuery.getMaxCost() > 0L) {
            clauseList.add(ResourceUtils.replace(LTE_RANGE_CLAUSE, "name", "maxCost", "value", traceQuery.getMaxCost().toString()));
        }

        if (StringUtils.isNotBlank(traceQuery.getStartTime())) {
            Date date = TimeUtils.parseAsDate(traceQuery.getStartTime(), TimeUtils.DATE_TIME);
            clauseList.add(ResourceUtils.replace(GTE_RANGE_CLAUSE, "name", "start", "value", String.valueOf(date.getTime())));
        }

        if (StringUtils.isNotBlank(traceQuery.getEndTime())) {
            Date date = TimeUtils.parseAsDate(traceQuery.getEndTime(), TimeUtils.DATE_TIME);
            clauseList.add(ResourceUtils.replace(LTE_RANGE_CLAUSE, "name", "end", "value", String.valueOf(date.getTime())));
        }

        return String.join(",", clauseList);
    }

    public TraceDetailVo getDetailByTraceId(String traceId) {
        String query = ResourceUtils.replace(TRACE_DETAIL_QUERY, "traceId", traceId);
        String result = esClient.query(query);
        List<IndexSpan> indexSpans = JsonUtils.getValues(result, "hits.hits._source", IndexSpan.class);
        return TraceDetailVo.of(indexSpans);
    }
}
