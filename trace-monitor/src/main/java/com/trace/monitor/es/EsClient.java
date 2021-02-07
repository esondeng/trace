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

    private String searchUrl;

    @PostConstruct
    public void init() {
        searchUrl = esUrl + "/trace/_search";
    }

    public String query(String query) {
        return HttpClientUtils.post(searchUrl, query);
    }
}
