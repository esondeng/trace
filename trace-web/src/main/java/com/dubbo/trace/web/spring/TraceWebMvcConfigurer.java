package com.dubbo.trace.web.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dubbo.trace.web.servlet.TracingInterceptor;

/**
 * @author dengxiaolin
 * @since 2021/01/22
 */
@Configuration
public class TraceWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TracingInterceptor()).addPathPatterns("/**");
    }
}
