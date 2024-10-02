package com.example.demo.controller;

import com.example.demo.config.RocketmqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountController {
    private static final Logger logger = LoggerFactory.getLogger(CountController.class);

    @Autowired
    private RocketmqConfig rocketmqConfig;

    @GetMapping("/world")
    public String world() {
        return "更新了0925";
    }


    @GetMapping("/pt")
    public String printMqConfig() {
        logger.info("++++");
        logger.info(rocketmqConfig.getNameServer());
        logger.info(rocketmqConfig.getProducer().getGroup());
        logger.info(rocketmqConfig.getProducer().getTopic());
        logger.info("*****");
        logger.info(rocketmqConfig.getConsumer().getGroup());
        logger.info(rocketmqConfig.getConsumer().getTopic());
        logger.info("++++");
        return rocketmqConfig.toString();
    }
}
