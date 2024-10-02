package com.example.demo;

import com.example.demo.config.RocketmqConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
//@EnableConfigurationProperties({RocketMQConfig.class})
@ContextConfiguration(classes = {RocketmqConfig.class})
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

}
