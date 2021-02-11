package com.trace.core.enums;


/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
public enum ServiceType {
    HTTP(0, "http"),
    DUBBO_CONSUMER(2, "duubo-c"),
    DUBBO_ASYNC_CONSUMER(3, "dubbo-async-c"),
    DUBBO_PROVIDER(4, "dubbo-p"),
    INNER_CALL(5, "inner-call"),
    ASYNC(6, "async-call"),
    JDBC(7, "jdbc"),
    MQ_SENDER(8, "mq-p"),
    MQ_CONSUMER(9, "mq-c");

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

