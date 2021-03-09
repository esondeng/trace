package com.trace.monitor.manager;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.trace.monitor.BaseTest;
import com.trace.monitor.query.DependencyQuery;
import com.trace.monitor.query.TraceQuery;
import com.trace.monitor.vo.DependencyVo;
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
        List<TraceVo> traceVoList = traceManager.getTraceVosByQuery(traceQuery);
        System.out.println(traceVoList.size());
    }

    @Test
    void testDependency() {
        DependencyQuery query = new DependencyQuery();
        query.setStartTime("2021-01-26 10:12:28");
        query.setEndTime("2021-01-27 11:14:28");

        List<DependencyVo> dependencyVos = traceManager.getDependencyVos(query);
        System.out.println(dependencyVos.size());
    }
}
