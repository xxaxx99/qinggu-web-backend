package com.lzh.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 * @author lzh
 */
@RestController
@RequestMapping("/health")
@Slf4j
public class TestController {

    @GetMapping
    public String testCheck() {
        return "ok";
    }

}
