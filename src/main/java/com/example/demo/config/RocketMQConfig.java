package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQConfig {
    private String nameServer;
    private Producer producer;
    private Consumer consumer;

    // Getters and Setters for all fields

    public static class Producer {
        private String group;
        private String topic;
        // Getter and Setter
        public String getGroup() {
            return group;
        }
        public void setGroup(String group) {
            this.group = group;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getTopic() {
            return topic;
        }
    }

    public static class Consumer {
        private String group;
        private String topic;
        // Getters and Setters
        public String getGroup() {
            return group;
        }
        public void setGroup(String group) {
            this.group = group;
        }
        public String getTopic() {
            return topic;
        }
        public void setTopic(String topic) {
            this.topic = topic;
        }
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
}
