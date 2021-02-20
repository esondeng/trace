package com.trace.monitor.enums;

import com.eson.common.core.enums.EnumBase;

/**
 * @author dengxiaolin
 * @since 2021/02/08
 */
public enum DateType implements EnumBase {
    /**
     * 时间类型
     */
    LAST_1_HOUR(0, "last1h"),
    LAST_3_HOUR(1, "last3h"),
    LAST_6_HOUR(2, "last6h"),
    LAST_24_HOUR(3, "last24h"),
    TODAY(4, "today"),
    CURRENT_MONTH(5, "currentMonth");

    private int id;
    private String message;

    DateType(int id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String message() {
        return this.message;
    }
}
