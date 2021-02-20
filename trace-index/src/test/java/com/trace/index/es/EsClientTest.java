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
        indexSpanList.addAll(JsonUtils.parseList("[{\"traceId\":\"fc4f3253f8374ef1a6af01b60ed8dd1b\",\"name\":\"ProductController.test\",\"id\":\"0\",\"depth\":1,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.25.161.78\",\"start\":1613809512895,\"end\":1613809512943,\"cost\":48,\"serviceType\":\"http\",\"errorMessage\":\"[\\\"com.dubbo.example.product.service.ProductService.sayHello : ArrayIndexOutOfBoundsException\\\"]\",\"status\":\"failed\",\"tagMap\":{\"httpPath\":\"/test/dubbo/test\",\"httpMethod\":\"GET\",\"httpUrl\":\"/test/dubbo/test\"}},{\"traceId\":\"fc4f3253f8374ef1a6af01b60ed8dd1b\",\"name\":\"Test.test\",\"id\":\"0.1\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.25.161.78\",\"start\":1613809512895,\"end\":1613809512896,\"cost\":1,\"serviceType\":\"inner-call\",\"status\":\"success\"},{\"traceId\":\"fc4f3253f8374ef1a6af01b60ed8dd1b\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2\",\"depth\":2,\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.25.161.78\",\"start\":1613809512896,\"end\":1613809512941,\"cost\":45,\"serviceType\":\"duubo-c\",\"status\":\"success\"}] \n", IndexSpan.class));

        indexSpanList.addAll(JsonUtils.parseList("[{\"traceId\":\"fc4f3253f8374ef1a6af01b60ed8dd1b\",\"name\":\"ProductService.sayHello\",\"id\":\"0.2.1\",\"depth\":3,\"clientAppKey\":\"dubbo-consumer-demo\",\"clientIp\":\"172.25.161.78\",\"appKey\":\"dubbo-provider-demo\",\"ip\":\"172.25.161.78\",\"start\":1613809512897,\"end\":1613809512939,\"cost\":42,\"serviceType\":\"dubbo-p\",\"errorMessage\":\"[\\\"java.lang.ArrayIndexOutOfBoundsException: 1\\\",\\\"com.dubbo.example.product.manager.ProductManager.test(ProductManager.java:25)\\\",\\\"com.dubbo.example.product.service.ProductServiceImpl.sayHello(ProductServiceImpl.java:21)\\\"]\",\"status\":\"failed\",\"tagMap\":{\"request\":\"[]\"}},{\"traceId\":\"fc4f3253f8374ef1a6af01b60ed8dd1b\",\"name\":\"TestMapper.getAll\",\"id\":\"0.2.1.1\",\"depth\":4,\"clientAppKey\":\"dubbo-consumer-demo\",\"clientIp\":\"172.25.161.78\",\"appKey\":\"dubbo-provider-demo\",\"ip\":\"172.25.161.78\",\"start\":1613809512898,\"end\":1613809512938,\"cost\":40,\"serviceType\":\"jdbc\",\"status\":\"success\",\"tagMap\":{\"jdbcRef\":\"jdbc:mysql://10.24.90.86:5002/waimai_agent\",\"sql\":\"select id, name, valid, ctime, utime from wm_agent_test; Total: 19\"}}]\n", IndexSpan.class));

        esClient.bulkUploadSpans(indexSpanList);
    }


    @Test
    void bulkTestLogs() {
        List<IndexLog> indexLogList = new ArrayList<>();
        indexLogList.addAll(JsonUtils.parseList("[{\n" +
                "\t\"appKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"traceId\": \"d1420bcad8204cd981d0eeaf2397d6e4\",\n" +
                "\t\"loggerName\": \"com.dubbo.example.product.controller.Test\",\n" +
                "\t\"thread\": \"http-nio-8416-exec-7\",\n" +
                "\t\"logTime\": 1613720493472,\n" +
                "\t\"logLevel\": \"INFO\",\n" +
                "\t\"message\": \"[\\\"test\\\\n\\\"]\"\n" +
                "}, {\n" +
                "\t\"appKey\": \"dubbo-consumer-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"traceId\": \"d1420bcad8204cd981d0eeaf2397d6e4\",\n" +
                "\t\"loggerName\": \"com.trace.dubbo.filter.TraceDubboConsumerFilter\",\n" +
                "\t\"thread\": \"http-nio-8416-exec-7\",\n" +
                "\t\"logTime\": 1613720493473,\n" +
                "\t\"logLevel\": \"INFO\",\n" +
                "\t\"message\": \"[\\\"RequestParam: []\\\\n\\\"]\"\n" +
                "}]", IndexLog.class));

        indexLogList.addAll(JsonUtils.parseList("[{\"appKey\":\"dubbo-consumer-demo\",\"ip\":\"172.25.161.78\",\"traceId\":\"d1420bcad8204cd981d0eeaf2397d6e4\",\"loggerName\":\"com.trace.dubbo.filter.TraceDubboConsumerFilter\",\"thread\":\"http-nio-8416-exec-7\",\"logTime\":1613720494023,\"logLevel\":\"INFO\",\"message\":\"[\\\"Response: AppResponse [value=null, exception=com.eson.common.core.exception.ServiceException: 1]\\\\n\\\"]\"}]", IndexLog.class));
        indexLogList.addAll(JsonUtils.parseList("[{\n" +
                "\t\"appKey\": \"dubbo-provider-demo\",\n" +
                "\t\"ip\": \"172.25.161.78\",\n" +
                "\t\"traceId\": \"d1420bcad8204cd981d0eeaf2397d6e4\",\n" +
                "\t\"loggerName\": \"org.apache.dubbo.rpc.filter.ExceptionFilter\",\n" +
                "\t\"thread\": \"DubboServerHandler-172.25.161.78:20880-thread-3\",\n" +
                "\t\"logTime\": 1613720494017,\n" +
                "\t\"logLevel\": \"ERROR\",\n" +
                "\t\"message\": \"[\\\"Got unchecked and undeclared exception which called by 172.25.161.78. service: com.dubbo.example.product.service.ProductService, method: sayHello, exception: java.lang.ArrayIndexOutOfBoundsException: 1\\\\njava.lang.ArrayIndexOutOfBoundsException: 1\\\\n\\\\tat com.dubbo.example.product.manager.ProductManager.test(ProductManager.java:25) ~[classes/:?]\\\\n\\\\tat com.dubbo.example.product.service.ProductServiceImpl.sayHello(ProductServiceImpl.java:21) ~[classes/:?]\\\\n\\\\tat org.apache.dubbo.common.bytecode.Wrapper1.invokeMethod(Wrapper1.java) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory$1.doInvoke(JavassistProxyFactory.java:47) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.proxy.AbstractProxyInvoker.invoke(AbstractProxyInvoker.java:84) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.config.invoker.DelegateProviderMetaDataInvoker.invoke(DelegateProviderMetaDataInvoker.java:56) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.InvokerWrapper.invoke(InvokerWrapper.java:56) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat com.trace.dubbo.filter.ServiceExceptionFilter.invoke(ServiceExceptionFilter.java:37) ~[trace-dubbo-1.0-SNAPSHOT.jar:?]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.monitor.support.MonitorFilter.invoke(MonitorFilter.java:89) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.filter.TimeoutFilter.invoke(TimeoutFilter.java:46) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) ~[dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat com.trace.dubbo.filter.TraceDubboProviderFilter.lambda$invoke$0(TraceDubboProviderFilter.java:45) ~[trace-dubbo-1.0-SNAPSHOT.jar:?]\\\\n\\\\tat com.trace.core.manager.TraceManager.invoke(TraceManager.java:87) [?:?]\\\\n\\\\tat com.trace.core.manager.TraceManager.tracingWithReturn(TraceManager.java:35) [?:?]\\\\n\\\\tat com.trace.dubbo.filter.TraceDubboProviderFilter.invoke(TraceDubboProviderFilter.java:40) [trace-dubbo-1.0-SNAPSHOT.jar:?]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.dubbo.filter.TraceFilter.invoke(TraceFilter.java:77) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.filter.ContextFilter.invoke(ContextFilter.java:129) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.filter.GenericFilter.invoke(GenericFilter.java:152) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.filter.ClassLoaderFilter.invoke(ClassLoaderFilter.java:38) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.filter.EchoFilter.invoke(EchoFilter.java:41) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:83) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol$1.reply(DubboProtocol.java:145) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.handleRequest(HeaderExchangeHandler.java:100) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.received(HeaderExchangeHandler.java:175) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.remoting.transport.DecodeHandler.received(DecodeHandler.java:51) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat org.apache.dubbo.remoting.transport.dispatcher.ChannelEventRunnable.run(ChannelEventRunnable.java:57) [dubbo-2.7.8.jar:2.7.8]\\\\n\\\\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [?:1.8.0_172]\\\\n\\\\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [?:1.8.0_172]\\\\n\\\\tat java.lang.Thread.run(Thread.java:748) [?:1.8.0_172]\\\\n\\\",\\\"com.dubbo.example.product.manager.ProductManager.test(ProductManager.java:25)\\\",\\\"com.dubbo.example.product.service.ProductServiceImpl.sayHello(ProductServiceImpl.java:21)\\\",\\\"org.apache.dubbo.common.bytecode.Wrapper1.invokeMethod(Wrapper1.java)\\\",\\\"org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory$1.doInvoke(JavassistProxyFactory.java:47)\\\",\\\"org.apache.dubbo.rpc.proxy.AbstractProxyInvoker.invoke(AbstractProxyInvoker.java:84)\\\",\\\"org.apache.dubbo.config.invoker.DelegateProviderMetaDataInvoker.invoke(DelegateProviderMetaDataInvoker.java:56)\\\"]\"\n" +
                "}]", IndexLog.class));

        esClient.bulkUploadLogs(indexLogList);
    }
}
