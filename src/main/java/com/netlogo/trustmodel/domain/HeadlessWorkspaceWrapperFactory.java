package com.netlogo.trustmodel.domain;

import lombok.val;
import org.nlogo.headless.HeadlessWorkspace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HeadlessWorkspaceWrapperFactory {
    private static final String MODEL_DIRECTORY = "models/";

    @Value("${netlogo-wrapper.model-filename}")
    private String modelFilename;

    public HeadlessWorkspaceWrapper create() throws IOException {
        val workspace = HeadlessWorkspace.newInstance();

        workspace.open(new ClassPathResource(MODEL_DIRECTORY + modelFilename).getURL().getPath());

        return new HeadlessWorkspaceWrapper(workspace);
    }
}
