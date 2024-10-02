package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("rocketmq")
public class RocketmqConfig {
    private String nameServer;
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    @Data
    public class Producer {
        private String group;
        private String topic;
    }

    @Data
    public class Consumer {
        private String group;
        private String topic;
    }
}
