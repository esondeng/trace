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

    public static final String SQL_TAG_KEY = "sql";

    public static final String HTTP_PATH_TAG_KEY = "httpPath";

    public static final String REQUEST_TAG_KEY = "request";
}
