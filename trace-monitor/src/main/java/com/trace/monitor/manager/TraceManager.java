package com.trace.monitor.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.eson.common.web.vo.PageVo;
import com.trace.monitor.query.TraceQuery;
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
}
