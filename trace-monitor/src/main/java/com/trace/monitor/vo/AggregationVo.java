package com.trace.monitor.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/21
 */
@Getter
@Setter
public class AggregationVo {
    private String name;
    private long count;

    public static AggregationVo of(String name, long count) {
        AggregationVo vo = new AggregationVo();
        vo.setName(name);
        vo.setCount(count);

        return vo;
    }
}
