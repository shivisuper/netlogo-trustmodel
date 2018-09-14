package com.netlogo.trustmodel.domain;


import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.nlogo.agent.Patch;
import org.nlogo.agent.Turtle;
import org.nlogo.api.Agent;
import org.nlogo.api.AgentException;
import org.nlogo.api.Color;
import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.nvm.RuntimePrimitiveException;
import org.nlogo.plot.PlotPen;
import org.springframework.util.Assert;
import scala.collection.JavaConverters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HeadlessWorkspaceWrapper {
    private static final String BREED_KEY = "BREED";

    private static final String COLOR_KEY = "COLOR";

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

    public synchronized String exportView() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return encodeImageToBase64(workspace.exportView());
    }

    public synchronized void wave() {
        Assert.isTrue(isReady(), "workspace is not ready");
        workspace.command("wave");
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

    public synchronized Map<Long, Map<String, String>> turtles() throws AgentException {
        Assert.isTrue(isReady(), "workspace is not ready");

        val world = workspace.world();
        val program = world.program();

        val turtleVariables = JavaConverters.seqAsJavaList(program.turtlesOwn());

        val turtleMap = new HashMap<Long, Map<String, String>>();
        for (final Agent agent : world.turtles().agents()) {
            val turtle = (Turtle) agent;

            val variablesMap = IntStream.range(0, turtleVariables.size()).boxed()
                    .collect(Collectors.toMap(
                            i -> turtleVariables.get(i).toUpperCase(),
                            i -> Objects.toString(turtle.getTurtleVariable(i), "")
                    ));

            // Overwrite BREED variable since its toString() function isn't helpful
            val breedName = turtle.getBreed().printName();
            variablesMap.put(BREED_KEY, breedName);

            // Overwrite COLOR variable with it's HEX equivalent
            val color = Color.getColor(turtle.color());
            variablesMap.put(COLOR_KEY, String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));

            val breedVariables = JavaConverters.seqAsJavaList(JavaConverters.mapAsJavaMap(program.breeds()).get(breedName).owns());

            for (final String breedVariable : breedVariables) {
                if (!turtleVariables.contains(breedVariable)) {
                    variablesMap.put(breedVariable, Objects.toString(turtle.getBreedVariable(breedVariable), ""));
                }
            }

            turtleMap.put(turtle.id(), variablesMap);
        }

        return turtleMap;
    }

    public synchronized Map<Long, Map<String, String>> patches() throws AgentException {
        Assert.isTrue(isReady(), "workspace is not ready");

        val world = workspace.world();
        val program = world.program();

        val patchVariables = JavaConverters.seqAsJavaList(program.patchesOwn());

        val patchMap = new HashMap<Long, Map<String, String>>();
        for (final Agent agent : world.patches().agents()) {
            val patch = (Patch) agent;

            val variablesMap = IntStream.range(0, patchVariables.size()).boxed()
                    .collect(Collectors.toMap(
                            i -> patchVariables.get(i).toUpperCase(),
                            i -> Objects.toString(patch.getPatchVariable(i), "")
                    ));

            // Overwrite COLOR variable with it's HEX equivalent
            val color = Color.getColor(patch.pcolor());
            variablesMap.put(COLOR_KEY, String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));


            patchMap.put(patch.id(), variablesMap);
        }

        return patchMap;
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

    public synchronized List<Plot> plots() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return Stream.of(workspace.plotManager().getPlotNames())
                .map(pn -> workspace.plotManager().getPlot(pn))
                .map(Plot::create)
                .collect(Collectors.toList());
    }

    private String encodeImageToBase64(@NonNull final BufferedImage image) {
        String imageString = null;

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);

            imageString = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageString;
    }

    // This method is to Casting the Division by Zero exception from Workspace
    private java.lang.Object reportToWorkSpace(String value) {
        java.lang.Object castingObject;
        try {
            castingObject = workspace.report(value);
        } catch (Exception ex) {
            castingObject = "N/A";
        }

        return castingObject;
    }

    @Value
    public static class Plot {
        @NonNull
        private String name;

        private double xMin;

        private double xMax;

        private double yMin;

        private double yMax;

        private boolean autoPlotOn;

        private boolean legendShown;

        private List<Pen> pens;

        public static Plot create(@NonNull org.nlogo.plot.Plot plot) {
            return new Plot(
                    plot.name(),
                    plot.xMin(),
                    plot.xMax(),
                    plot.yMin(),
                    plot.yMax(),
                    plot.autoPlotOn(),
                    plot.legendIsOpen(),
                    StreamSupport.stream(JavaConverters.asJavaIterable(plot.pens()).spliterator(), true)
                            .map(Pen::create)
                            .collect(Collectors.toList())
            );
        }

        @Value
        public static class Pen {
            @NonNull
            private String name;

            @NonNull
            private PenMode mode;

            @NonNull
            private String color;

            private boolean inLegend;

            @NonNull
            private List<Point> points;

            public static Pen create(@NonNull PlotPen plotPen) {
                return new Pen(
                        plotPen.name(),
                        PenMode.valueOf(plotPen.mode()),
                        Utils.convertArgbColorToHexString(plotPen.color()),
                        plotPen.inLegend(),
                        StreamSupport.stream(JavaConverters.asJavaIterable(plotPen.points()).spliterator(), true)
                                .map(plotPoint -> Point.of(plotPoint.x(), plotPoint.y()))
                                .collect(Collectors.toList())
                );
            }

            @Value(staticConstructor = "of")
            public static class Point {
                private double x;

                private double y;
            }

            public enum PenMode {
                LINE,
                BAR,
                POINT;

                public static PenMode valueOf(final int mode) {
                    if (mode == 0) {
                        return LINE;
                    } else if (mode == 1) {
                        return BAR;
                    } else if (mode == 2) {
                        return POINT;
                    } else {
                        throw new IllegalArgumentException("unexpected mode: " + mode);
                    }
                }
            }
        }
    }

    @UtilityClass
    private static class Utils {
        public String convertNetLogoColorToHexString(final double netLogoColor) {
            val color = Color.getColor(netLogoColor);

            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }

        public String convertArgbColorToHexString(final int argbColor) {
            return convertNetLogoColorToHexString(Color.argbToColor(argbColor));
        }
    }
}
