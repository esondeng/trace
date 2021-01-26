package com.trace.monitor;

import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.trace.monitor.spring.MonitorConfig;

/**
 * @author dengxiaolin
 * @since 2021/01/23
 */
@Configuration
@Import({
        DispatcherServletAutoConfiguration.class,
        EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        ConfigurationPropertiesAutoConfiguration.class,
        ProjectInfoAutoConfiguration.class,
        ThymeleafAutoConfiguration.class,
        MonitorConfig.class
})
public class MonitorApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MonitorApplication.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MonitorApplication.class);
    }
}
