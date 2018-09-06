package com.netlogo.trustmodel.controllers;

import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapper;
import com.netlogo.trustmodel.domain.HeadlessWorkspaceWrapperFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
        reportMap.put("clientsGreater5Yrs_Label", "count clients with [ xcor > 250 ]");
        reportMap.put("injurySeverity_Label","mean [ InjurySeverity ] of clients");
        reportMap.put("atFaultStatus_Label","mean [ AtFaultStatus ] of clients");
        reportMap.put("healthStatus_Label", "mean [ HealthStatus ] of clients");
        reportMap.put("previousInjury_Label","mean [ PreviousInjury ] of clients");
        reportMap.put("embeddedness_Label","mean [ Embeddedness ] of clients");
        reportMap.put("employmentStatus_Label", "mean [ EmploymentStatus ] of clients");
        reportMap.put("vulnerableStatus_Label", "mean [ VulnerableStatus ] of clients");
        reportMap.put("gender_Label", "mean [ Gender ] of clients");
        reportMap.put("age_Label", "mean [ Age ] of clients");
        reportMap.put("claimDuration_Label", "mean [ ClaimDuration ] of clients");
        reportMap.put("injuryClassification_Label", "mean [ InjuryClassification ] of clients");
        reportMap.put("education_Label", "mean [ Education ] of clients");
        reportMap.put("drift_Label", "mean [ Drift ] of clients");
        reportMap.put("waitListEffect_Label","mean [ Waitlisteffect ] of clients");
        reportMap.put("driftWaitListEffect_Label","mean [ Drift - Waitlisteffect ] of clients");
        reportMap.put("currentDrift_Label","CurrentDrift");
        reportMap.put("time_Label","time");
        reportMap.put("recalculateDrift_Label","RecalculateDrift");
        reportMap.put("costs_Label","costs");

        reportMap.put("meanRecoveryStatus_Label","mean [ ycor ] of clients + 100");
        reportMap.put("totalClients_Label","TotalClients");
        reportMap.put("exit_Label","TotalClients - count clients");
        reportMap.put("goodExit6Months_Label", "GoodExit6Months");
        reportMap.put("goodExit18Months_Label", "GoodExit18Months");
        reportMap.put("goodExit24Months_Label", "GoodExit24Months");
        reportMap.put("goodExit36Months_Label", "GoodExit36Months");
        reportMap.put("neutralExit36PlusMonths_Label","NeutralExit36PlusMonths");
        reportMap.put("bottom6Mo_Label", "BadExit6Months");
        reportMap.put("bottom18Mo_Label", "BadExit18Months");
        reportMap.put("bottom24Mo_Label", "BadExit24Months");
        reportMap.put("bottom36Mo_Label", "BadExit36Months");
        reportMap.put("bottom36+Mo_Label","BadExit36PlusMonths");


        //All Sliders initial setup values

        reportMap.put("newClients_Slider","NewClients");
        reportMap.put("roadSafetyEffectiveness_Slider","Road_Safety_Effectiveness");
        reportMap.put("injuryRecovery_Slider","InjuryRecovery");
        reportMap.put("randomVariation_Slider","RandomVariation");
        reportMap.put("recalculateDrift_Slider","RecalculateDrift");
        reportMap.put("shockZone1Starts_Slider","ShockZone1Starts");
        reportMap.put("shockZone1Ends_Slider","ShockZone1Ends");
        reportMap.put("shockZone2Starts_Slider","ShockZone2Starts");
        reportMap.put("shockZone2Ends_Slider","ShockZone2Ends");
        reportMap.put("reliefZone1Starts_Slider","ReliefZone1Starts");
        reportMap.put("reliefZone1Ends_Slider","ReliefZone1Ends");
        reportMap.put("reliefZone2Starts_Slider","ReliefZone2Starts");
        reportMap.put("reliefZone2Ends_Slider","ReliefZone2Ends");
        reportMap.put("shockZone1Increase_Slider","ShockZone1Increase");
        reportMap.put("shockZone2Increase_Slider","ShockZone2Increase");
        reportMap.put("reliefZone1Decrease_Slider","ReliefZone1Decrease");
        reportMap.put("reliefZone2Decrease_Slider","ReliefZone2Decrease");
        reportMap.put("driftModifier_Slider","DriftModifier");
        reportMap.put("solicitors_Slider","Solicitors");


//        reportMap.put("schemeType","scheme-type");
//        reportMap.put("shockZoneSD","ShockZoneSD");
//        reportMap.put("commonLawCapture", "CommonLawCapture");
//        reportMap.put("downwardDriftModifier", "DownwardDriftModifier");
//        reportMap.put("upwardDriftModifier", "UpwardDriftModifier");
//        reportMapLabels.put("percentgoodExit","100 * ( GoodExit6Months + GoodExit18Months + GoodExit24Months + GoodExit36Months ) / \n" +
//                "( TotalClients - count clients )");
//
//        reportMapLabels.put("%badExit","100 * count clients with [RockBottom = 1] \n" +
//                "/ \n" +
//                "(TotalClients - count clients)");
//        reportMapLabels.put("commonLaw#","CommonLaw#");
//        reportMapLabels.put("commonLaw%","100 * CommonLawCapture /\n" +
//                "(TotalClients - count clients)");


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

    @PostConstruct
    private void init() throws IOException {
        workspace = headlessWorkspaceWrapperFactory.create();
    }
}
