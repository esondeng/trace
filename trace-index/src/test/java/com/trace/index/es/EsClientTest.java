package com.trace.index.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.trace.common.domain.IndexSpan;
import com.trace.index.BaseTest;

/**
 * @author dengxiaolin
 * @since 2021/02/07
 */
public class EsClientTest extends BaseTest {
    @Autowired
    private EsClient esClient;

    @Test
    void bulkTest() {
        List<IndexSpan> indexSpanList = new ArrayList<>();

        IndexSpan indexSpan1 = new IndexSpan();
        indexSpan1.setTraceId("9c1dedea00464376afca73708e7d170b");
        indexSpan1.setId("0");
        indexSpan1.setDepth(1);
        indexSpan1.setName("ProductController.test");
        indexSpan1.setServiceType("http");
        indexSpan1.setAppKey("dubbo-consumer-demo");
        indexSpan1.setIp("172.25.166.99");
        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("httpPath", "/test/dubbo/test");
        indexSpan1.setTagMap(tagMap);
        indexSpan1.setStart(1611713548329L);
        indexSpan1.setEnd(1611713551201L);
        indexSpan1.setCost(2872);


        IndexSpan span2 = new IndexSpan();
        span2.setTraceId("9c1dedea00464376afca73708e7d170b");
        span2.setId("0.1");
        span2.setDepth(2);
        span2.setName("Test.test");
        span2.setServiceType("inner-call");
        span2.setAppKey("dubbo-consumer-demo");
        span2.setIp("172.25.166.99");
        span2.setStart(1611713548354L);
        span2.setEnd(1611713548363L);
        span2.setCost(9);

        IndexSpan span3 = new IndexSpan();
        span3.setTraceId("9c1dedea00464376afca73708e7d170b");
        span3.setId("0.2");
        span3.setDepth(2);
        span3.setName("ProductService.sayHello");
        span3.setServiceType("dubbo-c");
        span3.setAppKey("dubbo-consumer-demo");
        span3.setIp("172.25.166.99");
        span3.setStart(1611713548374L);
        span3.setEnd(1611713551176L);
        span3.setCost(2802);

        IndexSpan span4 = new IndexSpan();
        span4.setTraceId("9c1dedea00464376afca73708e7d170b");
        span4.setId("0.2.1");
        span4.setDepth(3);
        span4.setName("ProductService.sayHello");
        span4.setServiceType("dubbo-p");
        span4.setClientAppKey("dubbo-consumer-demo");
        span4.setClientIp("172.25.166.99");
        span4.setAppKey("dubbo-provider-demo");
        span4.setIp("172.25.166.99");
        span4.setStart(1611713548374L);
        span4.setEnd(1611713551176L);
        span4.setCost(2704);

        IndexSpan span5 = new IndexSpan();
        span5.setTraceId("9c1dedea00464376afca73708e7d170b");
        span5.setId("0.2.1.1");
        span5.setDepth(4);
        span5.setName("TestMapper.getAll");
        span5.setServiceType("jdbc");
        span5.setClientAppKey("dubbo-consumer-demo");
        span5.setClientIp("172.25.166.99");
        span5.setAppKey("dubbo-provider-demo");
        span5.setIp("172.25.166.99");
        span5.setStart(1611713548374L);
        span5.setEnd(1611713551176L);
        span5.setCost(2704);

        Map<String, String> tagMap1 = new HashMap<>();
        tagMap1.put("jdbcRef", "jdbc:mysql://10.24.90.86:5002/waimai_agent");
        tagMap1.put("sql", "select id, name, valid, ctime, utime from wm_agent_test; Total: 19");
        span5.setTagMap(tagMap1);


        indexSpanList.add(indexSpan1);
        indexSpanList.add(span2);
        indexSpanList.add(span3);
        indexSpanList.add(span4);
        indexSpanList.add(span5);

        esClient.bulkUploadSpans(indexSpanList);
    }
}
