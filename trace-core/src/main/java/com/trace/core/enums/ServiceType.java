package com.trace.core.enums;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
public enum ServiceType {
    HTTP(0, "http"),
    DUBBO(1, "dubbo"),
    INNER_CALL(1, "innerCall");

    private int id;

    private String message;

    ServiceType(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int id() {
        return id;
    }

    public String message() {
        return message;
    }
}

