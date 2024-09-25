package com.example.demo.controller;

import com.example.demo.config.RocketMQConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class MqProducer {
    private static final Logger logger = LoggerFactory.getLogger(MqProducer.class);

    @Autowired
    private RocketMQConfig mqConfig;


    @GetMapping("/send_msg")
    public String mqSendTest() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        // Instantiate a producer group
        logger.info("mqSendTest");
        logger.info(mqConfig.toString());
        DefaultMQProducer producer = new DefaultMQProducer("demo_producer");
        // Specify name server addresses
        producer.setNamesrvAddr("rocketmq-nameserver.rocket-mq.svc.cluster.local:9876");
        producer.setTopics(Collections.singletonList("TestTopic"));
//        producer.setSocksProxyConfig("localhost:10909");
        // Launch the instance
        producer.start();

        // Create a message instance, specifying topic, tag and message body
        Message msg = new Message("TestTopic", "binzooo", "Hello RocketMQ".getBytes());
        // Send the message
        SendResult sendResult = producer.send(msg);
        logger.info(sendResult.toString());
        producer.shutdown();
        return sendResult.toString();
    }
}
