package com.trace.log.scribe.log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.eson.common.core.util.JsonUtils;
import com.trace.common.domain.IndexLog;
import com.trace.core.TraceConfig;
import com.trace.core.constants.TraceConstants;
import com.trace.core.util.NetworkUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/02/18
 */
@Plugin(name = "Scribe", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE)
@Slf4j
public class ScribeAppender extends AbstractAppender {
    private static final int UPLOAD_SIZE = 1024;
    private static final int MAX_STACK_DEPTH = 6;

    private static final String SELF_LOG_PACKAGE = "com.trace.log";
    private static final String DEVELOP_PACKAGE = "com.dubbo.example";
    private static final String TRACE_DUBBO_FILTER_PACKAGE = "com.trace.dubbo.filter";
    private static final String DUBBO_FILTER_PACKAGE = "org.apache.dubbo.rpc.filter.ExceptionFilter";
    private static final String MAIN_THREAD_NAME = "main";

    private static final String SYSTEM_PROP_COLLECT_URL = "log.collect.url";

    private String appKey;
    private String ip;
    private BlockingQueue<IndexLog> queue;
    private ArrayList<IndexLog> retryList;
    private volatile boolean isActive = true;
    private PatternLayout layout;

    private String logCollectUrl;

    /**
     * 默认睡1秒
     */
    private int interval = 1000;

    private ThreadPoolExecutor uploadExecutor = new ThreadPoolExecutor(
            1,
            1,
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(),
            new ThreadFactory() {
                private AtomicInteger atomicInteger = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "thread-log-collector" + atomicInteger.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    @PluginFactory
    public static ScribeAppender createAppender(@PluginAttribute(value = "name") String name) {
        String logCollectUrl = System.getProperty(SYSTEM_PROP_COLLECT_URL);
        return new ScribeAppender(name, logCollectUrl);
    }

    private ScribeAppender(String name, String logCollectUrl) {
        super(name, null, null, true, Property.EMPTY_ARRAY);

        this.appKey = TraceConfig.getAppKey();
        this.ip = NetworkUtils.getLocalIp();
        this.logCollectUrl = logCollectUrl;

        this.queue = new ArrayBlockingQueue<>(1024 * 8);
        this.retryList = new ArrayList<>(UPLOAD_SIZE);
        this.layout = PatternLayout.createDefaultLayout();

        startUploadWorker();
        addShutdownHook();
    }


    private void startUploadWorker() {
        uploadExecutor.execute(() -> {
            while (isActive) {
                List<IndexLog> total = new ArrayList<>(UPLOAD_SIZE * 2);
                List<IndexLog> currentRoundList = new ArrayList<>(UPLOAD_SIZE);
                queue.drainTo(currentRoundList, UPLOAD_SIZE);


                if (!retryList.isEmpty()) {
                    total.addAll(retryList);
                }
                total.addAll(currentRoundList);

                if (!upload(total)) {
                    retryList.addAll(currentRoundList);
                }
                retryList.clear();
                try {
                    Thread.sleep(interval);
                }
                catch (Exception e) {
                }
            }
        });
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // upload worker 停止工作
            isActive = false;

            List<IndexLog> logs = new ArrayList<>(UPLOAD_SIZE);
            queue.drainTo(logs, UPLOAD_SIZE);


            if (!retryList.isEmpty()) {
                logs.addAll(retryList);
                retryList.clear();
            }
            upload(logs);
        }));
    }


    private boolean upload(List<IndexLog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return true;
        }

        try {
            log.info(JsonUtils.toJson(logs));
            // HttpClientUtils.post(logCollectUrl, JsonUtils.toJson(logs));
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void append(LogEvent logEvent) {
        String loggerName = logEvent.getLoggerName();
        if (StringUtils.isBlank(loggerName)) {
            return;
        }

        if (loggerName.startsWith(SELF_LOG_PACKAGE)) {
            return;
        }

        if (!loggerName.startsWith(DEVELOP_PACKAGE)
                && !loggerName.startsWith(TRACE_DUBBO_FILTER_PACKAGE)
                && !loggerName.startsWith(DUBBO_FILTER_PACKAGE)) {
            return;
        }

        String threadName = logEvent.getThreadName();
        if (MAIN_THREAD_NAME.equals(threadName)) {
            return;
        }

        IndexLog indexLog = new IndexLog();
        indexLog.setAppKey(appKey);
        indexLog.setIp(ip);
        indexLog.setLoggerName(loggerName);
        indexLog.setThread(threadName);
        indexLog.setTraceId(logEvent.getContextData().getValue(TraceConstants.TRACE_ID));
        indexLog.setLogTime(logEvent.getTimeMillis());
        indexLog.setLogLevel(logEvent.getLevel().toString());
        indexLog.setMessage(buildMessage(logEvent));

        queue.offer(indexLog);
    }

    private String buildMessage(LogEvent logEvent) {
        List<String> messages = new ArrayList<>();
        String message = this.layout.toSerializable(logEvent);
        messages.add(message);

        Throwable throwable = logEvent.getThrown();
        if (throwable != null) {
            StackTraceElement[] stackTraceElements = throwable.getStackTrace();
            int stackDepth = Math.min(stackTraceElements.length, MAX_STACK_DEPTH);

            for (int i = 0; i < stackDepth; i++) {
                messages.add(stackTraceElements[i].toString());
            }
        }

        return JsonUtils.toJson(messages);
    }
}
