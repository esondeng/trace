package com.trace.monitor.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author dengxiaolin
 * @since 2021/01/23
 */
@Configuration
@ComponentScan({"com.trace.monitor"})
@PropertySource("classpath:config/${spring.profiles.active:dev}/elasticsearch.properties")
public class MonitorConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
