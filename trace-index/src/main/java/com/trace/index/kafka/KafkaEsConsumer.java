package com.trace.index.kafka;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import com.eson.common.core.util.JsonUtils;
import com.trace.common.domain.IndexSpan;
import com.trace.index.es.EsClient;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dengxiaolin
 * @since 2021/02/05
 */
@Slf4j
@Setter
public class KafkaEsConsumer {
    private String brokerList;
    private String groupId;
    private String topic;

    @Autowired
    private EsClient esClient;

    private KafkaConsumer<String, String> consumer;

    private Duration timeout = Duration.of(60, ChronoUnit.SECONDS);

    private ThreadPoolExecutor consumerExecutor = new ThreadPoolExecutor(
            1,
            1,
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(),
            new ThreadFactory() {
                private AtomicInteger atomicInteger = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "thread-trace-consumer" + atomicInteger.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    @PostConstruct
    public void init() {
        log.info("启动kafka监听,brokerList= " + brokerList + ", topic=" + topic);

//        Properties props = new Properties();
//        props.put("bootstrap.servers", brokerList);
//        props.put("group.id", groupId);
//        props.put("enable.auto.commit", "false");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        consumer = new KafkaConsumer<>(props);
//
//        run();
    }

    private void run() {
        consumerExecutor.submit(() -> {
            try {
                consumer.subscribe(Arrays.asList(topic));
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(timeout);
                    List<IndexSpan> indexSpans = new ArrayList<>();
                    for (ConsumerRecord<String, String> record : records) {
                        indexSpans.add(JsonUtils.convertValue(record.value(), IndexSpan.class));
                    }
                    try {
                        esClient.bulkUploadSpans(indexSpans);
                    }
                    catch (Exception e) {
                        log.error("消费kafak消息失败", e);
                    }
                    finally {
                        consumer.commitSync();
                    }
                }
            }
            catch (Exception e) {
                log.error("消费kafak消息失败", e);
            }
        });
    }
}
