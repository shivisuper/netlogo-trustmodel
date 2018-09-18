package com.netlogo.trustmodel.services;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper.World;
import com.netlogo.trustmodel.services.WorkspaceService.WorkspaceGoEvent;
import lombok.NonNull;
import lombok.val;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkspaceGoEventListener implements ApplicationListener<WorkspaceGoEvent> {
    private List<FluxSink<World>> fluxSinks = new ArrayList<>();

    private World lastWorld = World.EMPTY;

    public void addFluxSink(@NonNull final FluxSink<World> fluxSink) {
        fluxSinks.add(fluxSink);

        fluxSink.next(lastWorld);
    }

    @Override
    public void onApplicationEvent(WorkspaceGoEvent event) {
        fluxSinks.removeIf(FluxSink::isCancelled);

        val currentWorld = event.getWorld();

        fluxSinks.forEach(fs -> fs.next(currentWorld));

        lastWorld = currentWorld;
    }
}
