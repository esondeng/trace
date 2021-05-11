package com.trace.index.spring;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.trace.index.es.EsClient;
import com.trace.index.kafka.KafkaEsConsumer;

/**
 * @author dengxiaolin
 * @since 2021/02/04
 */
@Configuration
@ComponentScan({"com.trace.index"})
@PropertySource("classpath:config/${spring.profiles.active:dev}/elasticsearch.properties")
@PropertySource("classpath:config/${spring.profiles.active:dev}/kafka.properties")
public class IndexConfig {
    @Value("span.kafka.broker.list")
    private String spanBrokerList;

    @Value("log.kafka.broker.list")
    private String logBrokerList;

    @Autowired
    private EsClient esClient;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public KafkaEsConsumer spanKafkaConsumer() {
        KafkaEsConsumer spanKafkaEsConsumer = new KafkaEsConsumer();
        spanKafkaEsConsumer.setBrokerList(spanBrokerList);
        spanKafkaEsConsumer.setGroupId("spanGroup");
        spanKafkaEsConsumer.setTopic("span");
        spanKafkaEsConsumer.setMessageConsumer((ConsumerRecords<String, String> records) -> esClient.bulkUploadSpans(records));
        return spanKafkaEsConsumer;
    }

    @Bean
    public KafkaEsConsumer logKafkaConsumer() {
        KafkaEsConsumer logKafkaEsConsumer = new KafkaEsConsumer();
        logKafkaEsConsumer.setBrokerList(logBrokerList);
        logKafkaEsConsumer.setGroupId("logGroup");
        logKafkaEsConsumer.setTopic("log");
        logKafkaEsConsumer.setMessageConsumer((ConsumerRecords<String, String> records) -> esClient.bulkUploadLogs(records));
        return logKafkaEsConsumer;
    }
}
