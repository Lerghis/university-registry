package com.university.registry.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController
{
    @GetMapping("/ping")
    public String ping()
    {
        System.out.println("ping() was called by Spring");
        return "pong";
    }
}
