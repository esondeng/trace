package com.trace.monitor.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eson.common.core.util.Funs;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.ResourceUtils;
import com.eson.common.core.util.TimeUtils;
import com.trace.common.domain.IndexSpan;
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
    private static final String TRACE_IDS_QUERY = ResourceUtils.getResource("/es/traceIdsQuery.txt");
    private static final String TRACE_DETAIL_QUERY = ResourceUtils.getResource("/es/traceDetailQuery.txt");

    private static final String TERM_CLAUSE = "{\"term\":{\"${name}\":\"${value}\"}}";
    private static final String GTE_RANGE_CLAUSE = "{\"range\":{\"${name}\":{\"gte\":\"${value}\"}}}";
    private static final String LTE_RANGE_CLAUSE = "{\"range\":{\"${name}\":{\"lte\":\"${value}\"}}}";
    private static final String MATCH_RANGE_CLAUSE = "{\"match\":{\"${name}\":\"${value}\"}}";

    @Autowired
    private EsClient esClient;

    public List<TraceVo> getTraceVosByQuery(TraceQuery traceQuery) {
        String query = buildEsQuery(traceQuery);
        String result = esClient.query(query);
        List<String> traceIds = JsonUtils.getValues(result, "aggregations.traceIds.buckets.key", String.class);

        if (CollectionUtils.isEmpty(traceIds)) {
            return Collections.emptyList();
        }
        else {
            List<String> traceIdsQuery = Funs.map(
                    traceIds,
                    t -> ResourceUtils.replace(TERM_CLAUSE, "name", "traceId", "value", t));
            query = ResourceUtils.replace(TRACE_IDS_QUERY, "conditions", String.join(",", traceIdsQuery));
            result = esClient.query(query);

            List<IndexSpan> indexSpans = JsonUtils.getValues(result, "hits.hits._source", IndexSpan.class);
            return Funs.map(indexSpans, TraceVo::of);
        }
    }

    private String buildEsQuery(TraceQuery traceQuery) {
        String conditions = buildConditions(traceQuery);

        Map<String, String> map = new HashMap<>(16);
        map.put("conditions", conditions);
        map.put("resultCount", String.valueOf(traceQuery.getResultCount()));

        return ResourceUtils.replace(TRACE_QUERY, map);
    }

    private String buildConditions(TraceQuery traceQuery) {
        List<String> clauseList = new ArrayList<>();

        if (StringUtils.isNotBlank(traceQuery.getTraceId())) {
            // traceId精确查询
            clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "traceId", "value", traceQuery.getTraceId()));
            return String.join(",", clauseList);
        }
        else {
            fillClauseList(clauseList, traceQuery);
            return String.join(",", clauseList);
        }
    }

    private void fillClauseList(List<String> clauseList, TraceQuery traceQuery) {
        // 非traceId精确查找，appKey必传
        clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "appKey", "value", traceQuery.getApplicationName()));

        addCondition(
                () -> StringUtils.isNotBlank(traceQuery.getName()),
                () -> clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "name", "value", traceQuery.getName()))
        );

        addCondition(
                () -> StringUtils.isNotBlank(traceQuery.getIp()),
                () -> clauseList.add(ResourceUtils.replace(TERM_CLAUSE, "name", "ip", "value", traceQuery.getIp()))
        );

        addCondition(
                () -> traceQuery.getMinCost() != null && traceQuery.getMinCost() > 0L,
                () -> clauseList.add(ResourceUtils.replace(GTE_RANGE_CLAUSE, "name", "minCost", "value", traceQuery.getMinCost().toString()))
        );

        addCondition(
                () -> traceQuery.getMaxCost() != null && traceQuery.getMaxCost() > 0L,
                () -> clauseList.add(ResourceUtils.replace(LTE_RANGE_CLAUSE, "name", "maxCost", "value", traceQuery.getMaxCost().toString()))
        );

        addCondition(
                () -> StringUtils.isNotBlank(traceQuery.getExceptionInfo()),
                () -> clauseList.add(ResourceUtils.replace(MATCH_RANGE_CLAUSE, "name", "errorMessage", "value", traceQuery.getExceptionInfo()))
        );

        addCondition(
                () -> StringUtils.isNotBlank(traceQuery.getStartTime()),
                () -> {
                    Date date = TimeUtils.parseAsDate(traceQuery.getStartTime(), TimeUtils.DATE_TIME);
                    clauseList.add(ResourceUtils.replace(GTE_RANGE_CLAUSE, "name", "start", "value", String.valueOf(date.getTime())));
                }
        );

        addCondition(
                () -> StringUtils.isNotBlank(traceQuery.getEndTime()),
                () -> {
                    Date date = TimeUtils.parseAsDate(traceQuery.getEndTime(), TimeUtils.DATE_TIME);
                    clauseList.add(ResourceUtils.replace(LTE_RANGE_CLAUSE, "name", "end", "value", String.valueOf(date.getTime())));
                }
        );
    }

    private void addCondition(Supplier<Boolean> supplier, Runnable runnable) {
        if (supplier.get()) {
            runnable.run();
        }
    }

    public TraceDetailVo getDetailByTraceId(String traceId) {
        String query = ResourceUtils.replace(TRACE_DETAIL_QUERY, "traceId", traceId);
        String result = esClient.query(query);
        List<IndexSpan> indexSpans = JsonUtils.getValues(result, "hits.hits._source", IndexSpan.class);
        return TraceDetailVo.of(indexSpans);
    }
}
