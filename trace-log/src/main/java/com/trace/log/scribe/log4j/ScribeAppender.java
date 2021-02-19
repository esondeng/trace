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
    public static ScribeAppender createAppender(@PluginAttribute(value = "name", defaultString = "null") final String name,
                                                @PluginAttribute(value = "logCollectUrl", defaultString = "null") final String logCollectUrl) {
        return new ScribeAppender(name, logCollectUrl);
    }

    private ScribeAppender(String name, String logCollectUrl) {
        super(name, null, null, true, Property.EMPTY_ARRAY);

        this.appKey = TraceConfig.getAppKey();
        this.ip = NetworkUtils.getLocalIp();
        System.out.println("logCollectUrl==" + logCollectUrl);
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

        if (loggerName.startsWith("com.trace.log")) {
            return;
        }

        if (!loggerName.startsWith("com.dubbo.example")
                && !loggerName.startsWith("com.trace.dubbo.filter")
                && !loggerName.startsWith("org.apache.dubbo.rpc.filter.ExceptionFilter")) {
            return;
        }

        String threadName = logEvent.getThreadName();
        if ("main".equals(threadName)) {
            return;
        }

        IndexLog indexLog = new IndexLog();
        indexLog.setAppKey(appKey);
        indexLog.setIp(ip);
        indexLog.setLoggerName(loggerName);
        indexLog.setThread(threadName);
        indexLog.setTraceId(logEvent.getContextData().getValue("traceId"));
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
