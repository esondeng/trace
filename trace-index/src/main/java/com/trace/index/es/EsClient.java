package com.trace.index.es;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eson.common.core.constants.Constants;
import com.eson.common.core.util.HttpClientUtils;
import com.eson.common.core.util.JsonUtils;
import com.eson.common.core.util.Strings;
import com.eson.common.core.util.TimeUtils;
import com.trace.common.domain.IndexLog;
import com.trace.common.domain.IndexSpan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/02/05
 */
@Component
@Slf4j
public class EsClient {
    private static final String BULK_WITH_ID = "{\"index\" : {\"_id\" : \"{}\" }}";
    private static final String BULK_WITHOUT_ID = "{ \"index\": {} }";

    @Value(value = "${elasticsearch.url}")
    private String esUrl;

    private String spanBulkUrl;

    private String logBulkUrl;

    @PostConstruct
    public void init() {
        // es7.x之后已经没有type，默认会在index下建一个type:_doc;一个index只有一个type
        spanBulkUrl = esUrl + "/trace/_bulk";
        logBulkUrl = esUrl + "/log/_bulk";
    }


    public boolean bulkUploadSpans(List<IndexSpan> indexSpanList) {
        if (CollectionUtils.isEmpty(indexSpanList)) {
            return true;
        }

        StringBuilder sb = new StringBuilder(100000);
        indexSpanList.forEach(indexSpan -> {
            sb.append(Strings.of(BULK_WITH_ID, indexSpan.generateIndexId()));
            sb.append(Constants.NEW_LINE);
            sb.append(JsonUtils.toJson(indexSpan));
            sb.append(Constants.NEW_LINE);
        });

        HttpClientUtils.post(spanBulkUrl, sb.toString());
        return true;
    }

    public boolean bulkUploadLogs(List<IndexLog> indexLogList) {
        if (CollectionUtils.isEmpty(indexLogList)) {
            return true;
        }

        StringBuilder sb = new StringBuilder(100000);
        indexLogList.forEach(indexSpan -> {
            sb.append(BULK_WITHOUT_ID);
            sb.append(Constants.NEW_LINE);

            indexSpan.setLogDate(TimeUtils.formatAsDateTime(new Date(indexSpan.getLogTime())));
            sb.append(JsonUtils.toJson(indexSpan));
            sb.append(Constants.NEW_LINE);
        });

        HttpClientUtils.post(logBulkUrl, sb.toString());
        return true;
    }
}
