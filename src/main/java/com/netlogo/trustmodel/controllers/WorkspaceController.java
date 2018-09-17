package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper;
import com.netlogo.trustmodel.services.WorkspaceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    @NonNull
    private WorkspaceService workspaceService;

    @PostMapping("/setup")
    public ResponseEntity<?> setup() {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        workspace().setup();

        return ResponseEntity.ok(workspace().world());
    }

    @PostMapping("/go")
    public ResponseEntity<?> go() {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        workspace().go();

        return ResponseEntity.ok(workspace().world());
    }

    @PostMapping("/command")
    public ResponseEntity<?> command(@RequestBody final String source) {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        workspace().command(source);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/commands")
    public ResponseEntity<?> commands(@RequestBody final List<String> sources) {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        workspace().commands(sources);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<?> reports() {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace().world().getReportMap());
    }

    @GetMapping("/view")
    public ResponseEntity<?> view() {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace().world().getView());
    }

    @GetMapping("/plots")
    public ResponseEntity<?> plots() {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace().world().getPlots());
    }

    @GetMapping("/world")
    public ResponseEntity<?> world() {
        Assert.isTrue(workspace().isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace().world());
    }

    @GetMapping(value = "/stream-world", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> streamView() {
        return Flux.generate(sink -> {
            workspace().go();

            sink.next(workspace().world());
        });
    }

    @GetMapping("/ready")
    public ResponseEntity<?> ready() {
        return ResponseEntity.ok(workspace().isReady());
    }

    private HeadlessWorkspaceWrapper workspace() {
        return workspaceService.getCurrentWorkspace();
    }
}
