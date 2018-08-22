package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.services.Workspace;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FireModelController {
    @NonNull
    private final Workspace workspace;

    @Autowired
    public FireModelController(Workspace workspace) {
        this.workspace = workspace;
    }

    @GetMapping("/test/{name}")
    public String test(@PathVariable String name) {
        return String.format("Hello %s", name);
    }
}
