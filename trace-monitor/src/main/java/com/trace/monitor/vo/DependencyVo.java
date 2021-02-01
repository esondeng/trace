package com.trace.monitor.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/01
 */
@Getter
@Setter
public class DependencyVo {
    private String parent;
    private String child;

    private int callCount;
    private int errorCount;
}
