package com.netlogo.trustmodel.services;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.nlogo.headless.HeadlessWorkspace;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class Workspace {
    @NonNull
    public final HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
}
