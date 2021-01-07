package com.trace.core.constants;

import com.trace.core.ConsumerContext;

/**
 * @author dengxiaolin
 * @since 2021/01/07
 */
public class TraceConstants {
    public static final String APPKEY_CONFIG_FILE_NAME = "application.properties";
    public static final String APP_KEY_PROP_NAME = "dubbo.application.name";

    /**
     * 第一个span的调用者是虚拟的
     */
    public static final String DUMMY_SPAN_ID = "-1";

    public static final ConsumerContext DUMMY_CONSUMER_CONTEXT = new ConsumerContext();

    static {
        DUMMY_CONSUMER_CONTEXT.setConsumerChildId(DUMMY_SPAN_ID);
    }

    /**
     * 第一个span id
     */
    public static final String HEAD_SPAN_ID = "0";

    public static final String CONSUMER_CONTEXT = "consumerContext";
}
