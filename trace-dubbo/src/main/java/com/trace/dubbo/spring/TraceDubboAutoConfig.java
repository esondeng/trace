package com.trace.dubbo.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.trace.collect.TraceCollector;
import com.trace.core.aop.TraceAspect;

/**
 * @author dengxiaolin
 * @since 2021/01/22
 */
@Configuration
public class TraceDubboAutoConfig {

    /**
     * 启动trace aop扫描
     */
    @Bean
    public TraceAspect dubboTraceAspect() {
        return new TraceAspect();
    }

    @Bean
    public TraceCollector dubboTraceCollector() {
        return new TraceCollector();
    }
}
