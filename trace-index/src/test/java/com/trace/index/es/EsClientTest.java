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
        indexSpanList.addAll(JsonUtils.parseList("[{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"ProductController.test\",\"id\":\"0\",\"depth\":1,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803880,\"end\":1619181803962,\"cost\":82,\"serviceType\":\"http\",\"errorMessage\":\"[\\\"com.dubbo.example.product.service.ProductService.sayHello : ArrayIndexOutOfBoundsException\\\"]\",\"status\":\"failed\",\"tagMap\":{\"httpPath\":\"/test/dubbo/test\",\"httpMethod\":\"GET\",\"httpUrl\":\"/test/dubbo/test\"}},{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"Test.test\",\"id\":\"0.1\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803880,\"end\":1619181803880,\"cost\":0,\"serviceType\":\"inner-call\",\"status\":\"success\"},{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803880,\"end\":1619181803959,\"cost\":79,\"serviceType\":\"duubo-c\",\"errorMessage\":\"[\\\"com.dubbo.example.product.service.ProductService.sayHello : ArrayIndexOutOfBoundsException\\\"]\",\"status\":\"failed\"}] \n[{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"ProductController.test\",\"id\":\"0\",\"depth\":1,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803880,\"end\":1619181803962,\"cost\":82,\"serviceType\":\"http\",\"errorMessage\":\"[\\\"com.dubbo.example.product.service.ProductService.sayHello : ArrayIndexOutOfBoundsException\\\"]\",\"status\":\"failed\",\"tagMap\":{\"httpPath\":\"/test/dubbo/test\",\"httpMethod\":\"GET\",\"httpUrl\":\"/test/dubbo/test\"}},{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"Test.test\",\"id\":\"0.1\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803880,\"end\":1619181803880,\"cost\":0,\"serviceType\":\"inner-call\",\"status\":\"success\"},{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803880,\"end\":1619181803959,\"cost\":79,\"serviceType\":\"duubo-c\",\"errorMessage\":\"[\\\"com.dubbo.example.product.service.ProductService.sayHello : ArrayIndexOutOfBoundsException\\\"]\",\"status\":\"failed\"}] \n", IndexSpan.class));

        indexSpanList.addAll(JsonUtils.parseList("[{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2.1\",\"depth\":3,\"clientAppKey\":\"dubbo-consumer-demo\",\"clientIp\":\"172.22.60.159\",\"appKey\":\"dubbo-provider-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803882,\"end\":1619181803956,\"cost\":74,\"serviceType\":\"dubbo-p\",\"errorMessage\":\"[\\\"java.lang.ArrayIndexOutOfBoundsException: 1\\\",\\\"com.dubbo.example.product.manager.ProductManager.test(ProductManager.java:25)\\\",\\\"com.dubbo.example.product.service.ProductServiceImpl.sayHello(ProductServiceImpl.java:21)\\\"]\",\"status\":\"failed\",\"tagMap\":{\"request\":\"[]\"}},{\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"name\":\"TestMapper.getAll\",\"id\":\"0.2.1.1\",\"depth\":4,\"clientAppKey\":\"dubbo-consumer-demo\",\"clientIp\":\"172.22.60.159\",\"appKey\":\"dubbo-provider-demo\",\"ip\":\"172.22.60.159\",\"start\":1619181803883,\"end\":1619181803955,\"cost\":72,\"serviceType\":\"jdbc\",\"status\":\"success\",\"tagMap\":{\"jdbcRef\":\"jdbc:mysql://10.24.90.86:5002/waimai_agent\",\"sql\":\"select id, name, valid, ctime, utime from wm_agent_test; Total: 19\"}}]\n", IndexSpan.class));

        esClient.bulkUploadSpans(indexSpanList);
    }


    @Test
    void bulkTestLogs() {
        List<IndexLog> indexLogList = new ArrayList<>();
        indexLogList.addAll(JsonUtils.parseList("[{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"loggerName\":\"com.dubbo.example.product.controller.Test\",\"thread\":\"http-nio-8416-exec-2\",\"logTime\":1619181803880,\"logDate\":null,\"logLevel\":\"INFO\",\"message\":\"test\\n\"},{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"loggerName\":\"com.trace.dubbo.filter.TraceDubboConsumerFilter\",\"thread\":\"http-nio-8416-exec-2\",\"logTime\":1619181803880,\"logDate\":null,\"logLevel\":\"INFO\",\"message\":\"RequestParam: []\\n\"},{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.22.60.159\",\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"loggerName\":\"com.trace.dubbo.filter.TraceDubboConsumerFilter\",\"thread\":\"http-nio-8416-exec-2\",\"logTime\":1619181803959,\"logDate\":null,\"logLevel\":\"INFO\",\"message\":\"Response: AppResponse [value=null, exception=com.eson.common.core.exception.ServiceException: 1]\\n\"}] \n", IndexLog.class));

        indexLogList.addAll(JsonUtils.parseList("[{\"appKey\":\"dubbo-provider-demo\",\"ip\":\"172.22.60.159\",\"traceId\":\"4a724f579bac48b4b50d3ffd958938d6\",\"loggerName\":\"org.apache.dubbo.rpc.filter.ExceptionFilter\",\"thread\":\"DubboServerHandler-192.168.3.53:20880-thread-4\",\"logTime\":1619181803955,\"logDate\":null,\"logLevel\":\"ERROR\",\"message\":\"Got unchecked and undeclared exception which called by 192.168.3.53. service: com.dubbo.example.product.service.ProductService, method: sayHello, exception: java.lang.ArrayIndexOutOfBoundsException: 1\\njava.lang.ArrayIndexOutOfBoundsException: 1\\n\\tat com.dubbo.example.product.manager.ProductManager.test(ProductManager.java:25) ~[classes/:?]\\n\\tat com.dubbo.example.product.service.ProductServiceImpl.sayHello(ProductServiceImpl.java:21) ~[classes/:?]\\n\\tat org.apache.dubbo.common.bytecode.Wrapper1.invokeMethod(Wrapper1.java) ~[dubbo-2.7.8.jar:2.7.8]", IndexLog.class));
        esClient.bulkUploadLogs(indexLogList);
    }
}
