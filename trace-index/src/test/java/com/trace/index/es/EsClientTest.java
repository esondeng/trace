package com.trace.index.es;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eson.common.core.util.JsonUtils;
import com.trace.common.domain.IndexLog;
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
    void bulkTestSpans() {
        List<IndexSpan> indexSpanList = new ArrayList<>();
        indexSpanList.addAll(JsonUtils.parseList("[{\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"name\":\"ProductController.test\",\"id\":\"0\",\"depth\":1,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"192.168.202.104\",\"start\":1624960557339,\"end\":1624960557394,\"cost\":55,\"serviceType\":\"http\",\"status\":\"success\",\"tagMap\":{\"httpPath\":\"/test/dubbo/test\",\"httpMethod\":\"GET\",\"httpUrl\":\"/test/dubbo/test\"}},{\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"name\":\"Test.test\",\"id\":\"0.1\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"192.168.202.104\",\"start\":1624960557342,\"end\":1624960557343,\"cost\":1,\"serviceType\":\"inner-call\",\"status\":\"success\"},{\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"192.168.202.104\",\"start\":1624960557344,\"end\":1624960557390,\"cost\":46,\"serviceType\":\"duubo-c\",\"status\":\"success\"}] \n", IndexSpan.class));

        indexSpanList.addAll(JsonUtils.parseList("[{\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2.1\",\"depth\":3,\"clientAppKey\":\"dubbo-consumer-demo\",\"clientIp\":\"192.168.202.104\",\"appKey\":\"dubbo-provider-demo\",\"ip\":\"192.168.202.104\",\"start\":1624960557349,\"end\":1624960557388,\"cost\":39,\"serviceType\":\"dubbo-p\",\"status\":\"success\",\"tagMap\":{\"request\":\"[]\"}},{\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"name\":\"TestMapper.getById\",\"id\":\"0.2.1.1\",\"depth\":4,\"clientAppKey\":\"dubbo-consumer-demo\",\"clientIp\":\"192.168.202.104\",\"appKey\":\"dubbo-provider-demo\",\"ip\":\"192.168.202.104\",\"start\":1624960557384,\"end\":1624960557387,\"cost\":3,\"serviceType\":\"jdbc\",\"status\":\"success\",\"tagMap\":{\"jdbcRef\":\"jdbc:mysql://127.0.0.1:3306/test\",\"sql\":\"select id, name, valid, ctime, utime from wm_agent_test where id = 1; Total: 1\"}}]\n", IndexSpan.class));

        esClient.bulkUploadSpans(indexSpanList);
    }


    @Test
    void bulkTestLogs() {
        List<IndexLog> indexLogList = new ArrayList<>();
        indexLogList.addAll(JsonUtils.parseList("[{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"192.168.202.104\",\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"loggerName\":\"com.dubbo.example.product.controller.Test\",\"thread\":\"http-nio-8416-exec-4\",\"logTime\":1624960557342,\"logDate\":null,\"logLevel\":\"INFO\",\"message\":\"test\"},{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"192.168.202.104\",\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"loggerName\":\"com.trace.dubbo.filter.TraceDubboConsumerFilter\",\"thread\":\"http-nio-8416-exec-4\",\"logTime\":1624960557344,\"logDate\":null,\"logLevel\":\"INFO\",\"message\":\"RequestParam: []\"},{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"192.168.202.104\",\"traceId\":\"5d8a56af6ac4455094b07a06ba40ed1f\",\"loggerName\":\"com.trace.dubbo.filter.TraceDubboConsumerFilter\",\"thread\":\"http-nio-8416-exec-4\",\"logTime\":1624960557390,\"logDate\":null,\"logLevel\":\"INFO\",\"message\":\"Response: AppResponse [value=testName, exception=null]\"}] \n", IndexLog.class));

        esClient.bulkUploadLogs(indexLogList);
    }
}
