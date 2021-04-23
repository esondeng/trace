package com.dubbo.trace.web.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dubbo.trace.web.filter.TraceFilter;
import com.dubbo.trace.web.servlet.TraceHandlerInterceptor;
import com.trace.collect.spring.TraceCollectConfig;

/**
 * @author dengxiaolin
 * @since 2021/01/22
 */
@Configuration
@Import({
        TraceCollectConfig.class
})
public class TraceWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceHandlerInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        TraceFilter filter = new TraceFilter();
        FilterRegistrationBean<TraceFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setName("traceFilter");

        Map<String, String> params = new HashMap<>(16);
        registration.setInitParameters(params);

        registration.setOrder(1);
        return registration;
    }
}
