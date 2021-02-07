package com.trace.index.es;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eson.common.core.constants.Constants;
import com.eson.common.core.util.HttpClientUtils;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.Strings;
import com.trace.common.domain.IndexSpan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/02/05
 */
@Component
@Slf4j
public class EsClient {
    /**
     * es 索引名,
     * es7.x之后已经没有type，默认会在index下建一个type:_doc
     * 一个index只有一个type
     */
    private static final String SPAN_BULK_PATH = "/trace/_bulk";

    private static final String BULK_REQUEST_ID = "{\"index\" : {\"_id\" : \"{}\" }}";


    @Value(value = "${elasticsearch.url}")
    private String esUrl;


    public boolean bulkUploadSpans(List<IndexSpan> indexSpanList) {
        if (CollectionUtils.isEmpty(indexSpanList)) {
            return true;
        }

        StringBuilder sb = new StringBuilder(100000);
        String spanBulkUrl = esUrl + SPAN_BULK_PATH;
        indexSpanList.forEach(indexSpan -> {
            sb.append(Strings.of(BULK_REQUEST_ID, indexSpan.generateIndexId()));
            sb.append(Constants.NEW_LINE);
            sb.append(JsonUtils.toJson(indexSpan));
            sb.append(Constants.NEW_LINE);
        });

        HttpClientUtils.post(spanBulkUrl, sb.toString());
        return true;
    }
}
