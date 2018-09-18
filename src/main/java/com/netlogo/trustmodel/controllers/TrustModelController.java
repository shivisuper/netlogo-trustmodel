package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.services.TrustModelService;
import com.netlogo.trustmodel.services.WorkspaceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trust-model")
@RequiredArgsConstructor
public class TrustModelController {
    @NonNull
    private final WorkspaceService workspaceService;

    @NonNull
    private final TrustModelService trustModelService;

    @PostMapping("/register-reporters")
    public ResponseEntity<?> registerReporters() {
        Assert.isTrue(workspaceService.isReady(), "workspace is not ready");

        workspaceService.clearRegisteredReporters();
        workspaceService.registerReporters(trustModelService.generateReporterSourceMap());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setupWorkspace() {
        Assert.isTrue(workspaceService.isReady(), "workspace is not ready");

        workspaceService.clearRegisteredReporters();
        workspaceService.registerReporters(trustModelService.generateReporterSourceMap());

        workspaceService.setup();
        return ResponseEntity.ok().build();
    }

}
