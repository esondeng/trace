package com.trace.monitor.es;

import org.springframework.stereotype.Component;

import com.eson.common.core.util.ResourceUtils;

/**
 * @author dengxiaolin
 * @since 2021/02/05
 */
@Component
public class EsClient {
    private static final String SEARCH_URL = "/trace/span/_search";

    private static final String ES_QUERY_DIRECTORY = "/es";
    private static final String TRACE_QUERY = ResourceUtils.getResource(
            ES_QUERY_DIRECTORY,
            "traceQuery.txt");
    private static final String TRACE_DETAIL_QUERY = ResourceUtils.getResource(
            ES_QUERY_DIRECTORY,
            "traceDetailQuery.txt");


}
