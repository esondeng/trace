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
     * es 索引名
     */
    private static final String TRACE_INDEX_NAME = "trace";

    private static final String BULK_REQUEST_ID = "{\"index\" : { \"_id\" : \"{}\" } }";
    private static final String BULK_REQUEST_CONTENT = "{\"span\" : {}}";


    @Value(value = "${trace.collect.url}")
    private String esUrl;


    public boolean bulkUploadSpans(List<IndexSpan> indexSpanList) {
        if (CollectionUtils.isEmpty(indexSpanList)) {
            return true;
        }

        StringBuilder sb = new StringBuilder(100000);
        String spanBulkUrl = esUrl + Constants.SLASH + TRACE_INDEX_NAME + Constants.SLASH + "_bulk";
        indexSpanList.forEach(indexSpan -> {
            sb.append(Strings.of(BULK_REQUEST_ID, indexSpan.generateIndexId()));
            sb.append(Constants.NEW_LINE);
            sb.append(Strings.of(BULK_REQUEST_CONTENT, JsonUtils.toJson(indexSpan)));
            sb.append(Constants.NEW_LINE);
        });

        sb.deleteCharAt(sb.length() - 1);

        HttpClientUtils.post(spanBulkUrl, sb.toString());
        return true;
    }
}
