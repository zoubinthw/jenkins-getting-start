package com.example.demo.controller;

import com.example.demo.config.RocketMQConfig;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MqConsumer.class);

    @Autowired
    private RocketMQConfig mqConfig;

    @GetMapping("/get_msg")
    public void mqProcessTest() throws MQClientException {
        // Instantiate a consumer group
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqConfig.getConsumer().getGroup());
        // Specify name server addresses
        consumer.setNamesrvAddr(mqConfig.getNameServer());
        // Subscribe to a topic
        consumer.subscribe(mqConfig.getConsumer().getTopic(), "*");

        // Register a callback to process messages
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                logger.info("Message Received: {}\n", new String(msg.getBody()));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        // Launch the instance
        consumer.start();
        logger.info("Consumer Started.\n");
    }
}
