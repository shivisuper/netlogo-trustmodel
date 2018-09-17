package com.netlogo.trustmodel.services;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.World;
import com.netlogo.trustmodel.services.WorkspaceService.WorkspaceGoEvent;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkspaceGoEventListener implements ApplicationListener<WorkspaceGoEvent> {
    private List<FluxSink<World>> fluxSinks = new ArrayList<>();

    public void addFluxSink(@NonNull final FluxSink<World> fluxSink) {
        fluxSinks.add(fluxSink);
    }

    @Override
    public void onApplicationEvent(WorkspaceGoEvent event) {
        fluxSinks.forEach(fs -> fs.next(event.getWorld()));

        fluxSinks.removeIf(FluxSink::isCancelled);
    }
}
