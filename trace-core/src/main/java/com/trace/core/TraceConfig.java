package com.trace.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.trace.core.constants.TraceConstants;
import com.trace.core.manager.TraceManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/01/07
 */
@Slf4j
public class TraceConfig {

    private static String appKey;

    static {
        Properties props = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = null;

        if (classLoader != null) {
            log.info("Trying to find [{}] using context class loader.", TraceConstants.APPKEY_CONFIG_FILE_NAME);
            is = classLoader.getResourceAsStream(TraceConstants.APPKEY_CONFIG_FILE_NAME);
        }

        if (is == null) {
            classLoader = TraceManager.class.getClassLoader();
            if (classLoader != null) {
                log.debug("Trying to find [{}] using TraceConfig class loader.", TraceConstants.APPKEY_CONFIG_FILE_NAME);
                is = classLoader.getResourceAsStream(TraceConstants.APPKEY_CONFIG_FILE_NAME);
            }
        }

        if (is != null) {
            try {
                props.load(is);
            }
            catch (IOException e) {
                log.error("props.load ERROR {}", e);
            }
        }
        appKey = props.getProperty(TraceConstants.APP_KEY_PROP_NAME);
        log.info("props.getProperty {} {}", TraceConstants.APPKEY_CONFIG_FILE_NAME, appKey);
    }

    public static String getAppKey() {
        return appKey;
    }
}
