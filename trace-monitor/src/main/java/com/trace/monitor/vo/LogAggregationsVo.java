package com.trace.monitor.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/21
 */
@Getter
@Setter
public class LogAggregationsVo {
    private List<AggregationVo> aggregations;
    private double tickInterval;

    public static LogAggregationsVo of(List<AggregationVo> aggregations, double tickInterval) {
        LogAggregationsVo vo = new LogAggregationsVo();
        vo.setAggregations(aggregations);
        vo.setTickInterval(tickInterval);

        return vo;
    }
}
