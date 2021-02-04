package com.dubbo.trace.web.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author dengxiaolin
 * @since 2021/01/22
 */
@Configuration
@Import({TraceWebMvcConfig.class})
public class TraceWebAutoConfig {

}
