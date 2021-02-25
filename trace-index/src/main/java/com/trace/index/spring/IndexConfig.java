package com.trace.index.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

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
        return spanKafkaEsConsumer;
    }

    @Bean
    public KafkaEsConsumer logKafkaConsumer() {
        KafkaEsConsumer logKafkaEsConsumer = new KafkaEsConsumer();
        logKafkaEsConsumer.setBrokerList(logBrokerList);
        logKafkaEsConsumer.setGroupId("logGroup");
        logKafkaEsConsumer.setTopic("log");
        return logKafkaEsConsumer;
    }
}
