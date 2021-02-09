package com.trace.index.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author dengxiaolin
 * @since 2021/02/04
 */
@Configuration
@ComponentScan({"com.trace.index"})
@PropertySource("classpath:config/${spring.profiles.active:dev}/elasticsearch.properties")
@PropertySource("classpath:config/${spring.profiles.active:dev}/kafka.properties")
public class IndexConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}