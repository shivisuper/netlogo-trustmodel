package com.netlogo.trustmodel.services;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapperFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    @NonNull
    private final HeadlessWorkspaceWrapperFactory headlessWorkspaceWrapperFactory;

    private HeadlessWorkspaceWrapper currentWorkspace;

    public HeadlessWorkspaceWrapper getCurrentWorkspace() {
        return currentWorkspace;
    }

    @PostConstruct
    private void init() throws IOException {
        currentWorkspace = headlessWorkspaceWrapperFactory.create();
    }
}
