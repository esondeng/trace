package com.trace.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.trace.core.Span;
import com.trace.core.TraceContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/01/10
 */
@Slf4j
public class TraceCollector {
    private static final int UPLOAD_SIZE = 1024;

    /**
     * 最大上传间隔时间
     */
    private static final int MAX_INTERVAL = 16 * 1000;
    /**
     * 最小上传间隔时间
     */
    private static final int MIN_INTERVAL = 1000;

    private ArrayList<Span> retryList;

    private TraceContainer traceContainer;

    /**
     * 默认睡2秒
     */
    private volatile int interval = 2 * 1000;

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
                    return new Thread(r, "thread-trace-collector" + atomicInteger.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    public TraceCollector() {
        this.retryList = new ArrayList<>(UPLOAD_SIZE);
        this.traceContainer = TraceContainer.getInstance();

        startUploadWorker();
        addShutdownHook();
    }

    private void startUploadWorker() {
        uploadExecutor.execute(() -> {
            while (traceContainer.isActive()) {
                List<Span> total = new ArrayList<Span>(UPLOAD_SIZE * 2);
                List<Span> currentRoundList = new ArrayList<Span>(UPLOAD_SIZE);
                traceContainer.getQueue().drainTo(currentRoundList, UPLOAD_SIZE);

                log.debug("span uploader loop interval " + interval +
                        " upload " + currentRoundList.size() +
                        " retry " + retryList.size() +
                        " fail " + traceContainer.getAndResetFailCounter());

                if (!retryList.isEmpty()) {
                    total.addAll(retryList);
                }
                total.addAll(currentRoundList);

                if (!upload(total)) {
                    log.warn("Failed to upload spans in retryList, the size of spans is {}", retryList.size());
                    retryList.clear();
                    retryList.addAll(currentRoundList);
                }
                else {
                    retryList.clear();
                }

                try {
                    Thread.sleep(interval);
                }
                catch (Exception e) {
                    log.error("Span upload worker sleep error", e);
                }
            }
        });
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // upload worker 停止工作
            traceContainer.setActive(false);

            List<Span> spans = new ArrayList<Span>(UPLOAD_SIZE);
            traceContainer.getQueue().drainTo(spans, UPLOAD_SIZE);

            log.info("TraceContainer before shutdown upload " + spans.size() +
                    " retry " + retryList.size() +
                    " fail " + traceContainer.getAndResetFailCounter());

            if (!retryList.isEmpty()) {
                spans.addAll(retryList);
                retryList.clear();
            }
            if (!spans.isEmpty()) {
                boolean success = upload(spans);
                if (!success) {
                    log.warn("Failed to upload spans before shutdown, the size of spans is {}", spans.size());
                }
            }
        }));
    }

    private boolean upload(List<Span> spans) {
        int currentUploadSize = spans.size();

        if (currentUploadSize >= UPLOAD_SIZE && interval > MIN_INTERVAL) {
            interval = interval / 2;
        }
        else if (currentUploadSize < UPLOAD_SIZE && interval < MAX_INTERVAL) {
            interval = interval * 2;
        }

        spans.forEach(span -> {
            log.info(span.toString());
            if (span.getChildren() != null) {
                span.getChildren().forEach(t -> log.info(t.toString()));
            }
        });
        return true;
    }
}
