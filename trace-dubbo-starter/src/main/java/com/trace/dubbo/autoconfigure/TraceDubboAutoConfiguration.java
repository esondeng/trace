package com.trace.dubbo.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.trace.collect.spring.TraceCollectConfig;

/**
 * @author dengxiaolin
 * @since 2021/06/29
 */
@Configuration
@Import({TraceCollectConfig.class})
public class TraceDubboAutoConfiguration {
}
