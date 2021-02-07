package com.trace.monitor.es;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.trace.common.domain.IndexSpan;
import com.trace.monitor.BaseTest;

/**
 * @author dengxiaolin
 * @since 2021/02/07
 */
public class EsClientTest extends BaseTest {
    @Autowired
    private EsClient esClient;

    @Test
    void testQuery() {
        List<IndexSpan> indexSpanList = esClient.getSpansByTraceId("9c1dedea00464376afca73708e7d170b");
        System.out.println(indexSpanList.size());
    }
}
