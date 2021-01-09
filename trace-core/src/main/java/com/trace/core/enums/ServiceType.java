package com.trace.core.enums;

import com.eson.common.core.enums.EnumBase;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
public enum ServiceType implements EnumBase {
    HTTP(0, "http"),
    DUBBO_CONSUMER(2, "duubo-c"),
    DUBBO_ASYNC_CONSUMER(3, "dubbo-async-c"),
    DUBBO_PROVIDER(4, "dubbo-p"),
    INNER_CALL(5, "inner-call"),
    ASYNC(6, "async");

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

