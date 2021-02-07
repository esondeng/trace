package com.trace.index;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.trace.index.spring.IndexConfig;

/**
 * @author dengxiaolin
 * @since 2021/02/07
 */
@SpringJUnitConfig(
        classes = {
                IndexConfig.class
        })
@TestPropertySource(
        locations = {
                "classpath:application.properties"
        })
@SpringBootTest
@ActiveProfiles("test")
public class BaseTest {
}
