package com.trace.core.enums;

import com.eson.common.core.enums.EnumBase;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
public enum ServiceType implements EnumBase {
    HTTP(0, "http"),
    DUBBO(1, "dubbo"),
    INNER_CALL(2, "inner-call"),
    ASYNC(3, "async");

    private int id;

    private String message;

    ServiceType(int id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String message() {
        return message;
    }
}

