package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.Plot;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.View;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.World;
import com.netlogo.trustmodel.services.WorkspaceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    @NonNull
    private WorkspaceService workspaceService;

    @PostMapping("/setup")
    public ResponseEntity<?> setup() {
        workspaceService.setup();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/go")
    public ResponseEntity<?> go() {
        val tickCount = workspaceService.go();

        return ResponseEntity.ok(tickCount);
    }

    @PostMapping("/command")
    public ResponseEntity<?> command(@RequestBody final String source) {
        workspaceService.command(source);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<?> reports() {
        return ResponseEntity.ok(workspaceService.reports());
    }

    @GetMapping("/view")
    public ResponseEntity<?> view() {
        return ResponseEntity.ok(workspaceService.view());
    }

    @GetMapping("/plots")
    public ResponseEntity<?> plots() {
        return ResponseEntity.ok(workspaceService.plots());
    }

    @GetMapping("/world")
    public ResponseEntity<?> world() {
        return ResponseEntity.ok(workspaceService.world());
    }

    @GetMapping(value = "/stream-view", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<View>> streamView() {
        return workspaceService.createWorldFlux().map(w -> createServerSentEvent(w, World::getView));
    }

    @GetMapping(value = "/stream-reports", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Object>>> streamReports() {
        return workspaceService.createWorldFlux().map(w -> createServerSentEvent(w, World::getReportMap));
    }

    @GetMapping(value = "/stream-plots", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<List<Plot>>> streamPlots() {
        return workspaceService.createWorldFlux().map(w -> createServerSentEvent(w, World::getPlots));
    }

    @GetMapping("/ready")
    public ResponseEntity<?> ready() {
        return ResponseEntity.ok(workspaceService.isReady());
    }

    private <T> ServerSentEvent<T> createServerSentEvent(@NonNull final World world, @NonNull final Function<World, T> mapper) {
        val builder = ServerSentEvent.<T>builder();

        builder.id(Long.toString(world.getTickCount()));
        builder.data(mapper.apply(world));
        builder.retry(Duration.ofSeconds(90));

        return builder.build();
    }
}
