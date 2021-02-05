package com.trace.collect.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.trace.collect.TraceCollector;
import com.trace.collect.aop.TraceAspect;

/**
 * @author dengxiaolin
 * @since 2021/02/04
 */
@Configuration
@PropertySource("classpath:config/${spring.profiles.active:dev}/trace.properties")
public class TraceCollectAutoConfig {

    /**
     * 启动trace aop扫描
     */
    @Bean
    public TraceAspect traceAspect() {
        return new TraceAspect();
    }

    @Bean
    public TraceCollector traceCollector() {
        return new TraceCollector();
    }
}
