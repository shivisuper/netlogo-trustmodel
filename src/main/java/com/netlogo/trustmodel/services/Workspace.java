package com.netlogo.trustmodel.services;

import lombok.NonNull;
import org.nlogo.headless.HeadlessWorkspace;
import org.springframework.stereotype.Service;

@Service
public class Workspace {
    @NonNull
    private final HeadlessWorkspace headlessWorkspace;

    public Workspace(HeadlessWorkspace headlessWorkspace) {
        this.headlessWorkspace = headlessWorkspace;
    }
}
