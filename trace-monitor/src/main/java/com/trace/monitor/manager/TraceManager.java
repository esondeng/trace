package com.trace.monitor.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.eson.common.web.vo.PageVo;
import com.trace.monitor.domain.Span;
import com.trace.monitor.query.TraceQuery;
import com.trace.monitor.vo.SpanVo;
import com.trace.monitor.vo.TraceDetailVo;
import com.trace.monitor.vo.TraceVo;

/**
 * @author dengxiaolin
 * @since 2021/01/27
 */
@Component
public class TraceManager {
    public PageVo<TraceVo> search(TraceQuery traceQuery) {
        List<TraceVo> traceVos = new ArrayList<>();
        TraceVo vo = TraceVo.of(
                "ProductController.test",
                "9c1dedea00464376afca73708e7d170b",
                "2021-01-26 10:15:00",
                2872,
                "success"
        );
        traceVos.add(vo);

        return PageVo.of(traceQuery, 1, traceVos);
    }

    public TraceDetailVo getSpanVosByTraceId(String traceId) {
        List<SpanVo> spanVos = new ArrayList<>();
        Span span1 = new Span();
        span1.setTraceId("9c1dedea00464376afca73708e7d170b");
        span1.setId("0");
        span1.setDepth(1);
        span1.setName("ProductController.test");
        span1.setServiceType("http");
        span1.setAppKey("dubbo-consumer-demo");
        span1.setIp("172.25.166.99");

        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("httpPath", "/test/dubbo/test");
        span1.setTagMap(tagMap);

        span1.setStart(1611713548329L);
        span1.setEnd(1611713551201L);
        span1.setCost(2872);


        Span span2 = new Span();
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

        Span span3 = new Span();
        span3.setTraceId("9c1dedea00464376afca73708e7d170b");
        span3.setId("0.2");
        span3.setDepth(2);
        span3.setName("ProductService.sayHello");
        span3.setServiceType("dubbo-c");
        span3.setAppKey("dubbo-consumer-demo");
        span3.setIp("172.25.166.99");

        span3.setStart(1611713548374L);
        span3.setEnd(1611713551176L);
        span3.setErrorMessages(Arrays.asList("time out","dubbo rpc time out"));
        span3.setCost(2802);
        spanVos.add(SpanVo.of(span1, null));
        spanVos.add(SpanVo.of(span2, span1));
        spanVos.add(SpanVo.of(span3, span1));

        return TraceDetailVo.of(spanVos);
    }
}
