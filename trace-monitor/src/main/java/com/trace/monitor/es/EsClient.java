package com.trace.monitor.es;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eson.common.core.util.HttpClientUtils;

/**
 * @author dengxiaolin
 * @since 2021/02/05
 */
@Component
public class EsClient {
    @Value(value = "${elasticsearch.url}")
    private String esUrl;

    private String spanSearchUrl;
    private String logSearchUrl;

    @PostConstruct
    public void init() {
        spanSearchUrl = esUrl + "/trace/_search";
        logSearchUrl = esUrl + "/log/_search";
    }

    public String querySpan(String query) {
        return HttpClientUtils.post(spanSearchUrl, query);
    }

    public String queryLog(String query) {
        return HttpClientUtils.post(logSearchUrl, query);
    }
}
