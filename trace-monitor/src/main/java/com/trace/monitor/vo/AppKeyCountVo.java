package com.trace.monitor.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/28
 */
@Getter
@Setter
public class AppKeyCountVo {
    private String appKey;
    private int count;

    public static AppKeyCountVo of(String appKey, int count) {
        AppKeyCountVo vo = new AppKeyCountVo();
        vo.setAppKey(appKey);
        vo.setCount(count);

        return vo;
    }
}
