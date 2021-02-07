package com.trace.monitor.es;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eson.common.core.util.HttpClientUtils;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.ResourceUtils;
import com.trace.common.domain.IndexSpan;

/**
 * @author dengxiaolin
 * @since 2021/02/05
 */
@Component
public class EsClient {
    private static final String ES_QUERY_DIRECTORY = "/es";

    private static final String TRACE_QUERY = ResourceUtils.getResource("/es/traceQuery.txt");
    private static final String TRACE_DETAIL_QUERY = ResourceUtils.getResource("/es/traceDetailQuery.txt");


    @Value(value = "${elasticsearch.url}")
    private String esUrl;

    private String searchUrl;

    @PostConstruct
    public void init() {
        searchUrl = esUrl + "/trace/_search";
    }

    public List<IndexSpan> getSpansByTraceId(String traceId) {
        String query = ResourceUtils.replace(TRACE_DETAIL_QUERY, "traceId", traceId);
        String result = HttpClientUtils.post(searchUrl, query);

        return JsonUtils.getValues(result, "hits.hits._source", IndexSpan.class);
    }
}
