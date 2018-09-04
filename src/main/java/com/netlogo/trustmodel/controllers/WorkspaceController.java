package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapperFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    @NonNull
    private final HeadlessWorkspaceWrapperFactory headlessWorkspaceWrapperFactory;

    private HeadlessWorkspaceWrapper workspace;

    @GetMapping("/ready")
    public ResponseEntity<?> ready() {
        return ResponseEntity.ok(workspace.isReady());
    }

    @GetMapping("/reports")
    public ResponseEntity<?> reports() {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace.getReports());
    }

    @GetMapping(value = "/stream-model", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> streamModel() {
        return Flux.generate(sink -> {
            workspace.go();
            sink.next(workspace.getReports());
        }).buffer(1);
    }

    // This will run the setup command and also register the reporters we want to monitor
    @PostMapping("/setup")
    public ResponseEntity<?> setup(@RequestBody final Map<String, String> reportMap) {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.clearRegisteredReports();

        workspace.setup();

        workspace.registerReports(reportMap);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/go")
    public ResponseEntity<?> go() {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.go();

        return ResponseEntity.ok(workspace.getReports());
    }

    @PostMapping("/commands")
    public ResponseEntity<?> commands(@RequestBody final List<String> sources) {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.commands(sources);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-reports")
    public ResponseEntity<?> registerReports(@RequestBody final Map<String, String> reportMap) {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.registerReports(reportMap);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/clear-registered-reports")
    public ResponseEntity<?> clearRegisteredReports() {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.clearRegisteredReports();

        return ResponseEntity.ok().build();
    }

    @PostConstruct
    private void init() throws IOException {
        workspace = headlessWorkspaceWrapperFactory.create();
    }
}
