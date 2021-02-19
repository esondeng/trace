package com.trace.monitor.query;

import com.eson.common.web.query.PageQuery;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Getter
@Setter
public class LogQuery extends PageQuery {
    private String condition;
}
