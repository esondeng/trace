package com.trace.monitor.vo;

import java.util.Collections;
import java.util.List;

import com.eson.common.web.query.PageQuery;
import com.eson.common.web.vo.PageVo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/20
 */
@Getter
@Setter
public class LogPageVo<T> extends PageVo<T> {
    private long cost;

    public static <T> LogPageVo<T> of(PageQuery pageQuery, int total, long cost, List<T> list) {
        LogPageVo<T> vo = new LogPageVo<>();
        vo.setTotal(total);
        vo.setCost(cost);

        vo.setPageNum(pageQuery.getPageNum());
        int pageSize = pageQuery.getPageSize();
        vo.setPageSize(pageSize);

        vo.setTotalPage((int) Math.ceil((double) total / (double) pageSize));
        vo.setList(list == null ? Collections.emptyList() : list);

        return vo;
    }
}
