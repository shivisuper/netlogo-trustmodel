package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapperFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.nlogo.api.AgentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    @NonNull
    private final HeadlessWorkspaceWrapperFactory headlessWorkspaceWrapperFactory;

    private HeadlessWorkspaceWrapper workspace;

    /// Plot
    @Value("${plot-clientHealthStatus}")
    private String clientHealthStatus;

    @Value("${plot-personalModerator}")
    private String personalModerator;

    @Value("${plot-personalModeratorAtFalseStatus}")
    private String personalModeratorAtFalseStatus;

    @Value("${plot-clientStatusChart}")
    private String clientStatusChart;

    @Value("${plot-clientStatusChart3PlusYears}")
    private String clientStatusChart3PlusYears;

    // Sliders
    @Value("${slider-newClients}")
    private String newClients;

    @Value("${slider_roadSafetyEffectiveness}")
    private String roadSafetyEffectiveness;

    @Value("${slider_injuryRecovery}")
    private String injuryRecovery;
    @Value("${slider_randomVariation}")
    private String randomVariation;
    @Value("${slider_recalculateDrift}")
    private String slider_recalculateDrift;

    @Value("${slider_shockZone1Starts}")
    private String shockZone1Starts;

    @Value("${slider_shockZone1Ends}")
    private String shockZone1Ends;

    @Value("${slider_shockZone2Starts}")
    private String shockZone2Starts;

    @Value("${slider_shockZone2Ends}")
    private String shockZone2Ends;

    @Value("${slider_reliefZone1Starts}")
    private String reliefZone1Starts;

    @Value("${slider_reliefZone1Ends}")
    private String reliefZone1Ends;

    @Value("${slider_reliefZone2Starts}")
    private String reliefZone2Starts;

    @Value("${slider_reliefZone2Ends}")
    private String reliefZone2Ends;

    @Value("${slider_shockZone1Increase}")
    private String shockZone1Increase;

    @Value("${slider_shockZone2Increase}")
    private String shockZone2Increase;

    @Value("${slider_reliefZone1Decrease}")
    private String reliefZone1Decrease;

    @Value("${slider_reliefZone2Decrease}")
    private String reliefZone2Decrease;

    @Value("${slider_driftModifier}")
    private String driftModifier;

    @Value("${slider_solicitors}")
    private String solicitors;

    // Labels

    @Value("${label-clientsGreater5Yrs}")
    private String clientsGreater5Yrs;


    @Value("${label-injurySeverity}")
    private String injurySeverity;

    @Value("${label-atFaultStatus}")
    private String atFaultStatus;

    @Value("${label-healthStatus}")
    private String healthStatus;
    @Value("${label-previousInjury}")
    private String previousInjury;

    @Value("${label-embeddedness}")
    private String embeddedness;
    @Value("${label-employmentStatus}")
    private String employmentStatus;

    @Value("${label-vulnerableStatus}")
    private String vulnerableStatus;

    @Value("${label-gender}")
    private String gender;

    @Value("${label-age}")
    private String age;
    @Value("${label-claimDuration}")
    private String claimDuration;

    @Value("${label-injuryClassification}")
    private String injuryClassification;

    @Value("${label-education}")
    private String education;
    @Value("${label-drift}")
    private String drift;

    @Value("${label-waitListEffect}")
    private String waitListEffect;

    @Value("${label-driftWaitListEffect}")
    private String driftWaitListEffect;

    @Value("${label-currentDrift}")
    private String currentDrift;

    @Value("${label-time}")
    private String time;
    @Value("${label-recalculateDrift}")
    private String label_recalculateDrift;

    @Value("${label-costs}")
    private String costs;

    @Value("${label-meanRecoveryStatus}")
    private String meanRecoveryStatus;

    @Value("${label-totalClients}")
    private String totalClients;

    @Value("${label-exit}")
    private String exit;

    @Value("${label-goodExit6Months}")
    private String goodExit6Months;

    @Value("${label-goodExit18Months}")
    private String goodExit18Months;

    @Value("${label-goodExit24Months}")
    private String goodExit24Months;

    @Value("${label-goodExit36Months}")
    private String goodExit36Months;

    @Value("${label-neutralExit36PlusMonths}")
    private String neutralExit36PlusMonths;

    @Value("${label-bottom6Mo}")
    private String bottom6Mo;

    @Value("${label-bottom18Mo}")
    private String bottom18Mo;

    @Value("${label-bottom24Mo}")
    private String bottom24Mo;

    @Value("${label-bottom36Mo}")
    private String bottom36Mo;
    @Value("${label-bottom36PlusMo}")
    private String bottom36PlusMo;

    @Value("${label-commonLaw#}")
    private String commonLawNum;

    @Value("${label-countClient}")
    private String countClient;

    @Value("${label-commonLaw%}")
    private String commonLawPercent;

    @Value("${label-percentGoodExit}")
    private String percentGoodExit;

    @Value("${label-percentBadExit}")
    private String percentBadExit;


/////////////////////////////// End Global Variables

    @GetMapping("/ready")
    public ResponseEntity<?> ready() {
        return ResponseEntity.ok(workspace.isReady());
    }

    @GetMapping("/reports")
    public ResponseEntity<?> reports() {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace.getReports());
    }

    @GetMapping(value = "/stream-model", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> streamModel() {
        return Flux.generate(sink -> {
            workspace.go();
            sink.next(workspace.getReports());
        }).buffer(1);
    }

    // This will run the setup command and also register the reporters we want to monitor
    @PostMapping("/setup")
    public ResponseEntity<?> setup() {

        Map<String, String> reportMap = new HashMap<>();

        Assert.isTrue(workspace.isReady(), "workspace is not ready");
        workspace.clearRegisteredReports();
        workspace.setup();


        //TODO: set the seed values using a global constant instead
        //right now this is quite ugly. Maybe we can add this stuff to the properties file


        // All labels initial setup values
        reportMap.put("clientsGreater5Yrs_Label", clientsGreater5Yrs);
        reportMap.put("injurySeverity_Label", injurySeverity);
        reportMap.put("atFaultStatus_Label", atFaultStatus);
        reportMap.put("healthStatus_Label", healthStatus);
        reportMap.put("previousInjury_Label", previousInjury);
        reportMap.put("embeddedness_Label", embeddedness);
        reportMap.put("employmentStatus_Label", employmentStatus);
        reportMap.put("vulnerableStatus_Label",vulnerableStatus);
        reportMap.put("gender_Label", gender);
        reportMap.put("age_Label", age);
        reportMap.put("claimDuration_Label", claimDuration);
        reportMap.put("injuryClassification_Label", injuryClassification);
        reportMap.put("education_Label", education);
        reportMap.put("drift_Label", drift);
        reportMap.put("waitListEffect_Label", waitListEffect);
        reportMap.put("driftWaitListEffect_Label", driftWaitListEffect);
        reportMap.put("currentDrift_Label", currentDrift);
        reportMap.put("time_Label", "time");
        reportMap.put("recalculateDrift_Label", label_recalculateDrift);
        reportMap.put("costs_Label", costs);
        reportMap.put("meanRecoveryStatus_Label", meanRecoveryStatus);
        reportMap.put("totalClients_Label", totalClients);
        reportMap.put("exit_Label", exit);
        reportMap.put("goodExit6Months_Label", goodExit6Months);
        reportMap.put("goodExit18Months_Label", goodExit18Months);
        reportMap.put("goodExit24Months_Label", goodExit24Months);
        reportMap.put("goodExit36Months_Label", goodExit36Months);
        reportMap.put("neutralExit36PlusMonths_Label", neutralExit36PlusMonths);
        reportMap.put("bottom6Mo_Label", bottom6Mo);
        reportMap.put("bottom18Mo_Label", bottom18Mo);
        reportMap.put("bottom24Mo_Label", bottom24Mo);
        reportMap.put("bottom36Mo_Label", bottom36Mo);
        reportMap.put("bottom36PlusMo_Label", bottom36PlusMo);
        reportMap.put("commonLawNum_Label", commonLawNum);
        reportMap.put("countClient_Label", countClient);
        reportMap.put("commonLawPercent_Label", commonLawPercent);
        reportMap.put("percentGoodExit_Label", percentGoodExit);
        reportMap.put("percentBadExit_Label", percentBadExit);

        //All Sliders initial setup values

        reportMap.put("newClients_Slider", newClients);
        reportMap.put("roadSafetyEffectiveness_Slider", roadSafetyEffectiveness);
        reportMap.put("injuryRecovery_Slider", injuryRecovery);
        reportMap.put("randomVariation_Slider", randomVariation);
        reportMap.put("recalculateDrift_Slider", slider_recalculateDrift);
        reportMap.put("shockZone1Starts_Slider", shockZone1Starts);
        reportMap.put("shockZone1Ends_Slider", shockZone1Ends);
        reportMap.put("shockZone2Starts_Slider", shockZone2Starts);
        reportMap.put("shockZone2Ends_Slider", shockZone2Ends);
        reportMap.put("reliefZone1Starts_Slider", reliefZone1Starts);
        reportMap.put("reliefZone1Ends_Slider", reliefZone1Ends);
        reportMap.put("reliefZone2Starts_Slider", reliefZone2Starts);
        reportMap.put("reliefZone2Ends_Slider", reliefZone2Ends);
        reportMap.put("shockZone1Increase_Slider", shockZone1Increase);
        reportMap.put("shockZone2Increase_Slider", shockZone2Increase);
        reportMap.put("reliefZone1Decrease_Slider", reliefZone1Decrease);
        reportMap.put("reliefZone2Decrease_Slider", reliefZone2Decrease);
        reportMap.put("driftModifier_Slider", driftModifier);
        reportMap.put("solicitors_Slider", solicitors);

        //All Plots initial setup values
        reportMap.put("clientHealthStatus_Plot", clientHealthStatus);
        reportMap.put("personalModerator_Plot", personalModerator);
        reportMap.put("personalModeratorAtFalseStatus_Plot", personalModeratorAtFalseStatus);
        reportMap.put("clientStatusChart_Plot", clientStatusChart);
        reportMap.put("clientStatusChart3PlusYears_Plot", clientStatusChart3PlusYears);

//        reportMap.put("ShockZone2Starts","ShockZone2Starts");
//        reportMap.put("ShockZone2Ends","ShockZone2Ends");

//        reportMap.put("tick","reset-ticks");
//        reportMap.put("days","days");
//        reportMap.put("schemeType","scheme-type");
//        reportMap.put("shockZoneSD","ShockZoneSD");
//        reportMap.put("downwardDriftModifier", "DownwardDriftModifier");
//        reportMap.put("upwardDriftModifier", "UpwardDriftModifier");

        workspace.registerReports(reportMap);

        return ResponseEntity.ok(workspace.getReports());
    }

    // TODO: return a stream of registered reporters
    @PostMapping("/go")
    public ResponseEntity<?> go() {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.go();

        return ResponseEntity.ok(workspace.getReports());
    }

    @PostMapping("/commands")
    public ResponseEntity<?> commands(@RequestBody final List<String> sources) {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.commands(sources);

        return ResponseEntity.ok().build();
    }

//    @PostMapping("/command/{source}")
//    public ResponseEntity<?> commands(@PathVariable("source") String source) {
//        Assert.isTrue(workspace.isReady(), "workspace is not ready");
//
//        // Check the source
//        System.out.println("command source is: "+ source);
////        workspace.command(source);
//
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/command")
    public ResponseEntity<?> commands(@RequestBody final String source) {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");
        workspace.command(source);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/wave")
    public ResponseEntity<?> wave() {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        workspace.wave();

        return ResponseEntity.ok(workspace.getReports());
    }

    @GetMapping("/turtles")
    public ResponseEntity<?> turtles() throws AgentException {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace.turtles());
    }

    @GetMapping("/patches")
    public ResponseEntity<?> patches() throws AgentException {
        Assert.isTrue(workspace.isReady(), "workspace is not ready");

        return ResponseEntity.ok(workspace.patches());
    }

    @PostConstruct
    private void init() throws IOException {
        workspace = headlessWorkspaceWrapperFactory.create();
    }

}
