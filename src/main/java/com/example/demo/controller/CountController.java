package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountController {

    @GetMapping("/world")
    public String world() {
        return "更新了0925";
    }
}
