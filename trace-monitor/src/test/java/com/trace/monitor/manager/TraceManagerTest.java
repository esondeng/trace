package com.trace.monitor.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eson.common.web.vo.PageVo;
import com.trace.monitor.BaseTest;
import com.trace.monitor.query.TraceQuery;
import com.trace.monitor.vo.TraceDetailVo;
import com.trace.monitor.vo.TraceVo;

/**
 * @author dengxiaolin
 * @since 2021/02/07
 */
public class TraceManagerTest extends BaseTest {
    @Autowired
    private TraceManager traceManager;

    @Test
    void testDetail() {
        TraceDetailVo traceDetailVo = traceManager.getDetailByTraceId("9c1dedea00464376afca73708e7d170b");
        System.out.println(traceDetailVo.getTraceId());
    }

    @Test
    void testQuery() {
        TraceQuery traceQuery = new TraceQuery();
        traceQuery.validate();

        traceQuery.setApplicationName("dubbo-consumer-demo");
        PageVo<TraceVo> tracePageVo = traceManager.getPageVoByQuery(traceQuery);
        System.out.println(tracePageVo.getTotal());
    }
}
