package com.netlogo.trustmodel.domain;


import lombok.NonNull;
import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.nvm.RuntimePrimitiveException;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HeadlessWorkspaceWrapper {
    private final HeadlessWorkspace workspace;

    private final Map<String, String> registeredReportMap = Collections.synchronizedMap(new HashMap<>());

    private boolean disposed = false;

    public HeadlessWorkspaceWrapper(@NonNull final HeadlessWorkspace workspace) {
        Assert.isTrue(workspace.modelOpened(), "workspace model must be opened");
        this.workspace = workspace;
        setup();
    }

    public synchronized void setup() {
        Assert.isTrue(isReady(), "workspace is not ready");
        workspace.command("setup");
    }

    public synchronized void go() {
        Assert.isTrue(isReady(), "workspace is not ready");
        workspace.command("go");
    }

    public synchronized void wave() {
        Assert.isTrue(isReady(), "workspace is not ready");
        workspace.command("wave");

        org.nlogo.api.Turtle turtle =(org.nlogo.api.Turtle)  workspace.world().turtles().getAgent(3.0);

        System.out.println("name turtle is "+ turtle);
        System.out.println("name turtle xcor is "+ turtle.xcor());
        System.out.println("name turtle ycor is "+ turtle.ycor());
        System.out.println("name turtle shape is "+ turtle.shape());
        System.out.println("name turtle color is "+ turtle.color());

//        org.nlogo.api.Turtle turtle =(org.nlogo.api.Turtle) workspace.world().turtles().getAgent(1);
//        System.out.println("[xcor] of turtle 3 = " + turtle.xcor());
//        workspace.dispose();
    }
    //TODO: will have to handle RuntimePrimitveException type that nlogo throws randomly
    //TODO: whenever some randomly generated reporters are empty
    public synchronized Map<String, Object> getReports() throws RuntimePrimitiveException {
            Assert.isTrue(isReady(), "workspace is not ready");

            return registeredReportMap.entrySet().stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(Entry::getKey,
                                    e -> reportToWorkSpace(e.getValue())),
                            Collections::unmodifiableMap
                    ));

    }

    // This method is to wrapping the Division by Zero exception from Workspace
    private java.lang.Object reportToWorkSpace(String value){
        java.lang.Object wrappingObject;
        try {
            wrappingObject=workspace.report(value);
        }
        catch (Exception ex)
        { wrappingObject="N/A";}

       return wrappingObject;
    }
    public synchronized void command(@NonNull final String source) {
        Assert.isTrue(isReady(), "workspace is not ready");

        workspace.command(source);
    }

    public synchronized void commands(@NonNull final Collection<String> sources) {
        Assert.isTrue(isReady(), "workspace is not ready");

        sources.forEach(this::command);
    }

    public synchronized void registerReport(@NonNull final String name, @NonNull final String source) {
        Assert.isTrue(isReady(), "workspace is not ready");
        Assert.hasText(name, "name must have text");
        Assert.hasText(source, "source");

        registeredReportMap.put(name, source);
    }

    public synchronized void registerReports(@NonNull final Map<String, String> reportMap) {
        Assert.isTrue(isReady(), "workspace is not ready");
        reportMap.forEach(this::registerReport);
    }

    public synchronized void clearRegisteredReports() {
        Assert.isTrue(isReady(), "workspace is not ready");
        registeredReportMap.clear();
    }

    public synchronized void dispose() throws InterruptedException {
        Assert.isTrue(isReady(), "workspace is not ready");
        disposed = true;
        workspace.dispose();
    }

    public boolean isReady() {
        return !disposed && workspace.modelOpened();
    }
}
