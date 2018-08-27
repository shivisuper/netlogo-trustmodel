package com.netlogo.trustmodel.services;

import lombok.NonNull;
import org.nlogo.headless.HeadlessWorkspace;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {
    private static final String MODEL_FOLDER_PATH = "models/";

    private HeadlessWorkspace workspace;


    public void loadModel(@NonNull final String modelFilename) throws IOException {
        Assert.hasText(modelFilename, "modelFilename must have text");

        workspace = HeadlessWorkspace.newInstance();

        try {
            workspace.open(new ClassPathResource(MODEL_FOLDER_PATH + modelFilename).getURL().getPath());
        } catch (final IOException ex) {
            workspace = null;

            throw ex;
        }
    }

    public boolean isModelLoaded() {
        return workspace != null && workspace.modelOpened();
    }

    public void command(@NonNull final String source) {
        Assert.isTrue(isModelLoaded(), "model must be loaded");

        workspace.command(source);
    }

    public void command(@NonNull final List<String> sources) {
        Assert.isTrue(isModelLoaded(), "model must be loaded");

        sources.forEach(this::command);
    }

    public Object report(String source) {
        Assert.isTrue(isModelLoaded(), "model must be loaded");

        return workspace.report(source);
    }

    public List<Object> report(List<String> sources) {
        Assert.isTrue(isModelLoaded(), "model must be loaded");

        return sources.stream().map(this::report).collect(Collectors.toList());
    }
}
