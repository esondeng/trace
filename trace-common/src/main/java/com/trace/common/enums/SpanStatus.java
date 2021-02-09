package com.trace.common.enums;

import com.eson.common.core.enums.EnumBase;

/**
 * @author dengxiaolin
 * @since 2021/02/08
 */
public enum SpanStatus implements EnumBase {
    SUCCESS(0, "success"),
    FAILED(1, "failed");

    private int id;
    private String message;

    SpanStatus(int id, String message) {
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
