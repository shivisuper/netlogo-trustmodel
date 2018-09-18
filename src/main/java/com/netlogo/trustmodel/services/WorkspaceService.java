package com.netlogo.trustmodel.services;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.Plot;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.View;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.World;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapperFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;

    @NonNull
    private final HeadlessWorkspaceWrapperFactory headlessWorkspaceWrapperFactory;

    @NonNull
    private final WorkspaceGoEventListener workspaceGoEventListener;

    private HeadlessWorkspaceWrapper workspace;

    public void setup() {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.setup();

        publishWorkspaceGoEvent();
    }

    public long go() {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.go();

        publishWorkspaceGoEvent();

        return workspace.world().getTickCount();
    }

    public void command(@NonNull final String... sources) {
        Assert.isTrue(isReady(), "workspace is not ready");

        Stream.of(sources).forEach(workspace::command);
    }

    public Map<String, Object> reports() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return workspace.world().getReportMap();
    }

    public View view() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return workspace.world().getView();
    }

    public List<Plot> plots() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return workspace.world().getPlots();
    }

    public World world() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return workspace.world();
    }

    public synchronized void registerReporter(@NonNull final String name, @NonNull final String source) {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.registerReporter(name, source);

        publishWorkspaceGoEvent();
    }

    public synchronized void registerReporters(@NonNull final Map<String, String> reportMap) {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.registerReporters(reportMap);

        publishWorkspaceGoEvent();
    }

    public synchronized void clearRegisteredReporters() {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.clearRegisteredReporters();

        publishWorkspaceGoEvent();
    }

    public synchronized void dispose() throws InterruptedException {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.dispose();
    }

    public boolean isReady() {
        return workspace.isReady();
    }

    public Flux<World> createWorldFlux() {
        return Flux.create(workspaceGoEventListener::addFluxSink);
    }

    @PostConstruct
    private void init() throws IOException {
        workspace = headlessWorkspaceWrapperFactory.create();

        publishWorkspaceGoEvent();
    }

    private void publishWorkspaceGoEvent() {
        applicationEventPublisher.publishEvent(new WorkspaceGoEvent(this, workspace.world()));
    }

    public static class WorkspaceGoEvent extends ApplicationEvent {
        @Getter
        private World world;

        WorkspaceGoEvent(Object source, final World world) {
            super(source);
            this.world = world;
        }
    }
}
