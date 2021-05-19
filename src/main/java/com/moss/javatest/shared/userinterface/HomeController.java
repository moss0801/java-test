package com.moss.javatest.shared.userinterface;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping
    public String index() {
        return "Hello, World!";
    }
}
