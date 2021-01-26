package com.dubbo.trace.web.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.trace.collect.TraceCollector;
import com.trace.collect.aop.TraceAspect;

/**
 * @author dengxiaolin
 * @since 2021/01/22
 */
@Configuration
@Import({TraceWebMvcConfig.class})
public class TraceWebAutoConfig {

    /**
     * 启动trace aop扫描
     */
    @Bean
    @ConditionalOnMissingBean(name = "dubboTraceAspect")
    public TraceAspect traceAspect() {
        return new TraceAspect();
    }

    @Bean
    @ConditionalOnMissingBean(name = "dubboTraceCollector")
    public TraceCollector traceCollector() {
        return new TraceCollector();
    }
}
