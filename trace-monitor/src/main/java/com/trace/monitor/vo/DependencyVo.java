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
    private String errorRate;
    private String tp90;
    private String tp99;
    private String tp999;
    private String tp9999;
}
