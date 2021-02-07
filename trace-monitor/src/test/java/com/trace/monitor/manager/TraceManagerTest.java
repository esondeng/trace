package com.trace.monitor.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.trace.monitor.BaseTest;
import com.trace.monitor.vo.TraceDetailVo;

/**
 * @author dengxiaolin
 * @since 2021/02/07
 */
public class TraceManagerTest extends BaseTest {
    @Autowired
    private TraceManager traceManager;

    @Test
    void test() {
        TraceDetailVo traceDetailVo = traceManager.getTraceDetailByTraceId("9c1dedea00464376afca73708e7d170b");
        System.out.println(traceDetailVo.getTraceId());
    }
}
