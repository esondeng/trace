package com.trace.core.constants;

import com.trace.core.Span;

/**
 * @author dengxiaolin
 * @since 2021/01/07
 */
public class TraceConstants {
    public static final String APP_KEY_CONFIG_FILE_NAME = "application.properties";
    public static final String APP_KEY_PROP_NAME = "dubbo.application.name";

    public static final Span DUMMY_SPAN = new Span();
    /**
     * 第一个span id
     */
    public static final String HEAD_SPAN_ID = "0";

    public static final String CONSUMER_CONTEXT = "consumerContext";
}
