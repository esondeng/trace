package com.trace.monitor.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/04
 */
@Getter
@Setter
public class DependencyQuery {
    private String applicationName;
    private String startTime;
    private String endTime;
}
