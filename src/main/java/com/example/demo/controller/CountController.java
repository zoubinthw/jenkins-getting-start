package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountController {

//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @GetMapping("/hello")
//    public String count() {
//        Long cntPeople = redisTemplate.opsForValue().increment("count-people");
//        return "有 [" + cntPeople + "] 人访问了这个页面";
//    }

    @GetMapping("/world")
    public String world() {
        return "有 1000 人访问了这个页面";
    }
}
