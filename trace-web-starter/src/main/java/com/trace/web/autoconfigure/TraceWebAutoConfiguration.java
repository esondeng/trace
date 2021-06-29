package com.trace.web.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.trace.collect.spring.TraceCollectConfig;
import com.trace.web.spring.TraceWebMvcConfig;

/**
 * @author dengxiaolin
 * @since 2021/06/29
 */
@Configuration
@Import({TraceWebMvcConfig.class, TraceCollectConfig.class})
public class TraceWebAutoConfiguration {
}
