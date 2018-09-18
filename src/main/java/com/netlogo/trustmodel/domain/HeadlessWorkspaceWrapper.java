package com.netlogo.trustmodel.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.experimental.Wither;
import lombok.val;
import org.nlogo.api.Color;
import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.plot.PlotPen;
import org.springframework.util.Assert;
import scala.collection.JavaConverters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HeadlessWorkspaceWrapper {
    private final HeadlessWorkspace workspace;

    private final Map<String, String> registeredReportMap = Collections.synchronizedMap(new HashMap<>());

    private World currentWorld;

    private boolean disposed = false;

    HeadlessWorkspaceWrapper(@NonNull final HeadlessWorkspace workspace) {
        Assert.isTrue(workspace.modelOpened(), "workspace model must be opened");

        this.workspace = workspace;

        setup();
    }

    public synchronized void setup() {
        executeCommand("setup");

        updateCurrentWorld();
    }

    public synchronized void go() {
        Assert.isTrue(isReady(), "workspace is not ready");

        executeCommand("go");

        updateCurrentWorld();
    }

    public synchronized void command(@NonNull final String source) {
        Assert.isTrue(isReady(), "workspace is not ready");

        executeCommand(source);
    }

    public World world() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return currentWorld;
    }


    public synchronized void registerReporter(@NonNull final String name, @NonNull final String source) {
        Assert.isTrue(isReady(), "workspace is not ready");
        Assert.hasText(name, "name must have text");
        Assert.hasText(source, "source");

        registeredReportMap.put(name, source);

        updateCurrentReports();
    }

    public synchronized void registerReporters(@NonNull final Map<String, String> reportMap) {
        Assert.isTrue(isReady(), "workspace is not ready");

        registeredReportMap.putAll(reportMap);

        updateCurrentReports();
    }

    public synchronized void clearRegisteredReporters() {
        Assert.isTrue(isReady(), "workspace is not ready");

        registeredReportMap.clear();

        updateCurrentReports();
    }

    public synchronized void dispose() throws InterruptedException {
        Assert.isTrue(isReady(), "workspace is not ready");

        disposed = true;
        workspace.dispose();
    }

    public boolean isReady() {
        return !disposed && workspace.modelOpened();
    }

    private Map<String, Object> generateCurrentReports() {
        val reportValueMap = new HashMap<String, Object>(registeredReportMap.size());

        registeredReportMap.forEach((k, v) -> reportValueMap.put(k, executeReporter(v)));

        return Collections.unmodifiableMap(reportValueMap);
    }

    private View generateCurrentView() {
        return View.create(workspace);
    }

    private List<Plot> generateCurrentPlots() {
        Assert.isTrue(isReady(), "workspace is not ready");

        return Stream.of(workspace.plotManager().getPlotNames())
                .map(pn -> workspace.plotManager().getPlot(pn))
                .map(Plot::create)
                .collect(Collectors.toList());
    }

    private synchronized void updateCurrentReports() {
        this.currentWorld = currentWorld.withReportMap(generateCurrentReports());
    }

    private synchronized void updateCurrentWorld() {
        this.currentWorld = new World(
                (long) workspace.world().ticks(),
                generateCurrentReports(),
                generateCurrentView(),
                generateCurrentPlots()
        );
    }

    private synchronized void executeCommand(@NonNull String source) {
        // TODO: Look into compiling commands into Procedures using workspace.compileCommands() to cut down on syntax parsing time

        workspace.command(source);
    }

    private Object executeReporter(@NonNull String source) {
        try {
            // TODO: Look into compiling reports into Procedures using workspace.compileReporter() to cut down on syntax parsing time
            return workspace.report(source);
        } catch (Exception ex) {
            return null;
        }
    }

    @Value
    @Wither
    public static class World {
        public static final World EMPTY = new World(0, Collections.emptyMap(), View.EMPTY, Collections.emptyList());

        private long tickCount;

        @NonNull
        @JsonProperty("reports")
        private Map<String, Object> reportMap;

        @NonNull
        private View view;

        @NonNull
        private List<Plot> plots;
    }

    @Value
    public static class View {
        public static final View EMPTY = new View(Utils.EMPTY_IMAGE_STRING, 1, 1);

        @NonNull
        private String imgSrc;

        private double height;

        private double width;

        static View create(@NonNull HeadlessWorkspace headlessWorkspace) {
            val image = headlessWorkspace.exportView();

            return new View(
                    Utils.encodeImageToBase64(image),
                    image.getHeight(),
                    image.getWidth()
            );
        }
    }

    @Value
    public static class Plot {
        @NonNull
        private String name;

        @JsonProperty("xMin")
        private double xMin;

        @JsonProperty("xMax")
        private double xMax;

        @JsonProperty("yMin")
        private double yMin;

        @JsonProperty("yMax")
        private double yMax;

        private boolean autoPlotOn;

        private boolean legendShown;

        private List<Pen> pens;

        static Plot create(@NonNull org.nlogo.plot.Plot plot) {
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
        static class Pen {
            @NonNull
            private String name;

            @NonNull
            private PenMode mode;

            @NonNull
            private String color;

            private boolean inLegend;

            @NonNull
            private List<Point> points;

            static Pen create(@NonNull PlotPen plotPen) {
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
            static class Point {
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
        String EMPTY_IMAGE_STRING = "data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=";

        String convertNetLogoColorToHexString(final double netLogoColor) {
            val color = Color.getColor(netLogoColor);

            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }

        String convertArgbColorToHexString(final int argbColor) {
            return convertNetLogoColorToHexString(Color.argbToColor(argbColor));
        }

        String encodeImageToBase64(@NonNull final BufferedImage image) {
            // Default to a 1x1 transparent GIF
            String imageString = EMPTY_IMAGE_STRING;

            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", outputStream);

                imageString = "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imageString;
        }
    }
}
