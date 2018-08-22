package com.netlogo.trustmodel.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FireModelController {
    @GetMapping("/test/{name}")
    public String test(@PathVariable String name) {
        return String.format("Hello %s", name);
    }
}
