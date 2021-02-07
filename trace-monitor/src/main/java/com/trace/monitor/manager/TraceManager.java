package com.trace.monitor.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eson.common.web.vo.PageVo;
import com.trace.common.domain.IndexSpan;
import com.trace.monitor.es.EsClient;
import com.trace.monitor.query.TraceQuery;
import com.trace.monitor.vo.TraceDetailVo;
import com.trace.monitor.vo.TraceVo;

/**
 * @author dengxiaolin
 * @since 2021/01/27
 */
@Component
public class TraceManager {
    @Autowired
    private EsClient esClient;

    public PageVo<TraceVo> search(TraceQuery traceQuery) {
        List<TraceVo> traceVos = new ArrayList<>();
        IndexSpan indexSpan = new IndexSpan();
        indexSpan.setTraceId("9c1dedea00464376afca73708e7d170b");
        indexSpan.setName("ProductController.test");
        indexSpan.setStart(1611713548329L);
        indexSpan.setCost(2872);

        traceVos.add(TraceVo.of(indexSpan));

        return PageVo.of(traceQuery, 1, traceVos);
    }

    public TraceDetailVo getTraceDetailByTraceId(String traceId) {
        List<IndexSpan> indexSpans = esClient.getSpansByTraceId(traceId);
        return TraceDetailVo.of(indexSpans);
    }
}
