package com.netlogo.trustmodel.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.nlogo.headless.HeadlessWorkspace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FireModelController {
    @NonNull
    private final HeadlessWorkspace headlessWorkspace;

    @GetMapping("/test/{name}")
    public String test(@PathVariable String name) {
        return String.format("Hello %s\nRunning headless: %s", name, headlessWorkspace.isHeadless());
    }
}
