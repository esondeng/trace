package com.trace.index.es;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eson.common.core.util.JsonUtils;
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
        indexSpanList.addAll(JsonUtils.parseList("[{\n" +
                "\t\"traceId\": \"198fc58b4ad74188ae1bcd44130e265e\",\n" +
                "\t\"name\": \"ProductController.test\",\n" +
                "\t\"id\": \"0\",\n" +
                "\t\"depth\": 1,\n" +
                "\t\"appKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"start\": 1612869248074,\n" +
                "\t\"end\": 1612869250910,\n" +
                "\t\"cost\": 2836,\n" +
                "\t\"serviceType\": \"http\",\n" +
                "\t\"status\": \"success\",\n" +
                "\t\"tagMap\": {\n" +
                "\t\t\"httpPath\": \"/test/dubbo/test\",\n" +
                "\t\t\"method\": \"GET\",\n" +
                "\t\t\"httpUrl\": \"/test/dubbo/test\"\n" +
                "\t}\n" +
                "}, {\n" +
                "\t\"traceId\": \"198fc58b4ad74188ae1bcd44130e265e\",\n" +
                "\t\"name\": \"Test.test\",\n" +
                "\t\"id\": \"0.1\",\n" +
                "\t\"depth\": 2,\n" +
                "\t\"appKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"start\": 1612869248100,\n" +
                "\t\"end\": 1612869248108,\n" +
                "\t\"cost\": 8,\n" +
                "\t\"serviceType\": \"inner-call\",\n" +
                "\t\"status\": \"success\"\n" +
                "}, {\n" +
                "\t\"traceId\": \"198fc58b4ad74188ae1bcd44130e265e\",\n" +
                "\t\"name\": \"ProductService.sayHello\",\n" +
                "\t\"id\": \"0.2\",\n" +
                "\t\"depth\": 2,\n" +
                "\t\"appKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"start\": 1612869248118,\n" +
                "\t\"end\": 1612869250884,\n" +
                "\t\"cost\": 2766,\n" +
                "\t\"serviceType\": \"duubo-c\",\n" +
                "\t\"status\": \"success\"\n" +
                "}]", IndexSpan.class));

        indexSpanList.addAll(JsonUtils.parseList("[{\n" +
                "\t\"traceId\": \"198fc58b4ad74188ae1bcd44130e265e\",\n" +
                "\t\"name\": \"ProductService.sayHello\",\n" +
                "\t\"id\": \"0.2.1\",\n" +
                "\t\"depth\": 3,\n" +
                "\t\"clientAppKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"clientIp\": \"172.25.161.78\",\n" +
                "\t\"appKey\": \"dubbo-provider-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"start\": 1612869248273,\n" +
                "\t\"end\": 1612869250864,\n" +
                "\t\"cost\": 2591,\n" +
                "\t\"serviceType\": \"dubbo-p\",\n" +
                "\t\"status\": \"success\",\n" +
                "\t\"tagMap\": {\n" +
                "\t\t\"request\": \"[]\"\n" +
                "\t}\n" +
                "}, {\n" +
                "\t\"traceId\": \"198fc58b4ad74188ae1bcd44130e265e\",\n" +
                "\t\"name\": \"TestMapper.getAll\",\n" +
                "\t\"id\": \"0.2.1.1\",\n" +
                "\t\"depth\": 4,\n" +
                "\t\"clientAppKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"clientIp\": \"172.25.161.78\",\n" +
                "\t\"appKey\": \"dubbo-provider-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"start\": 1612869250804,\n" +
                "\t\"end\": 1612869250857,\n" +
                "\t\"cost\": 53,\n" +
                "\t\"serviceType\": \"jdbc\",\n" +
                "\t\"status\": \"success\",\n" +
                "\t\"tagMap\": {\n" +
                "\t\t\"jdbcRef\": \"jdbc:mysql://10.24.90.86:5002/waimai_agent\",\n" +
                "\t\t\"sql\": \"select id, name, valid, ctime, utime from wm_agent_test; Total: 19\"\n" +
                "\t}\n" +
                "}]", IndexSpan.class));

        esClient.bulkUploadSpans(indexSpanList);
    }
}
