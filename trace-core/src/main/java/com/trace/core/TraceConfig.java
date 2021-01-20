package com.trace.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.trace.core.constants.TraceConstants;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/07
 */
public class TraceConfig {

    private static String appKey;

    static {
        Properties props = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = null;

        if (classLoader != null) {
            is = classLoader.getResourceAsStream(TraceConstants.APP_KEY_CONFIG_FILE_NAME);
        }

        if (is == null) {
            classLoader = TraceManager.class.getClassLoader();
            if (classLoader != null) {
                is = classLoader.getResourceAsStream(TraceConstants.APP_KEY_CONFIG_FILE_NAME);
            }
        }

        if (is != null) {
            try {
                props.load(is);
            }
            catch (IOException e) {
            }
        }
        appKey = props.getProperty(TraceConstants.APP_KEY_PROP_NAME);
    }

    public static String getAppKey() {
        return appKey;
    }
}
