package com.trace.monitor;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.trace.monitor.spring.MonitorConfig;

/**
 * @author dengxiaolin
 * @since 2021/02/07
 */
@SpringJUnitConfig(
        classes = {
                MonitorConfig.class
        })
@TestPropertySource(
        locations = {
                "classpath:application.properties"
        })
@SpringBootTest
@ActiveProfiles("test")
public class BaseTest {
}
