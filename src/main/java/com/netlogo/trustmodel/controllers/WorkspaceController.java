package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.services.WorkspaceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkspaceController {
    @NonNull
    private final WorkspaceService workspaceService;

    @PostMapping("/load-model")
    public ResponseEntity<?> loadModel(@RequestParam final String filename) throws IOException {
        workspaceService.loadModel(filename);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-model-loaded")
    public ResponseEntity<?> isModelLoaded() {
        return ResponseEntity.ok(workspaceService.isModelLoaded());
    }

    @PostMapping(value = "/command", params = "source")
    public ResponseEntity<?> command(@RequestParam final String source) {
        Assert.isTrue(workspaceService.isModelLoaded(), "model must be loaded");

        workspaceService.command(source);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/command")
    public ResponseEntity<?> command(@RequestBody final List<String> sources) {
        Assert.isTrue(workspaceService.isModelLoaded(), "model must be loaded");

        workspaceService.command(sources);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/report", params = "source")
    public ResponseEntity<?> report(@RequestParam final String source) {
        Assert.isTrue(workspaceService.isModelLoaded(), "model must be loaded");

        return ResponseEntity.ok(workspaceService.report(source));
    }

    @GetMapping("/report")
    public ResponseEntity<?> report(@RequestBody final List<String> sources) {
        Assert.isTrue(workspaceService.isModelLoaded(), "model must be loaded");

        return ResponseEntity.ok(workspaceService.report(sources));
    }
}
