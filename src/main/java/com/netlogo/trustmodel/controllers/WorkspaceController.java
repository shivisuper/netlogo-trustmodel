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
        Map<String, String> reportMapLabels = new HashMap<>();

        Assert.isTrue(workspace.isReady(), "workspace is not ready");
        workspace.clearRegisteredReports();
        workspace.setup();

        //TODO: set the seed values using a global constant instead
        //right now this is quite ugly. Maybe we can add this stuff to the properties file

        reportMapLabels.put("clientsGreater5Yrs", "count clients with [ xcor > 250 ]");
        reportMapLabels.put("injurySeverity","mean [ InjurySeverity ] of clients");
        reportMapLabels.put("atFaultStatus","mean [ AtFaultStatus ] of clients");
        reportMapLabels.put("healthStatus", "mean [ HealthStatus ] of clients");
        reportMapLabels.put("previousInjury","mean [ PreviousInjury ] of clients");
        reportMapLabels.put("embeddedness","mean [ Embeddedness ] of clients");
        reportMapLabels.put("employmentStatus", "mean [ EmploymentStatus ] of clients");
        reportMapLabels.put("vulnerableStatus", "mean [ VulnerableStatus ] of clients");
        reportMapLabels.put("gender", "mean [ Gender ] of clients");
        reportMapLabels.put("age", "mean [ Age ] of clients");
        reportMapLabels.put("claimDuration", "mean [ ClaimDuration ] of clients");
        reportMapLabels.put("injuryClassification", "mean [ InjuryClassification ] of clients");
        reportMapLabels.put("education", "mean [ Education ] of clients");
        reportMapLabels.put("drift", "mean [ Drift ] of clients");
        reportMapLabels.put("waitListEffect","mean [ Waitlisteffect ] of clients");
        reportMapLabels.put("driftWaitListEffect","mean [ Drift - Waitlisteffect ] of clients");
        reportMapLabels.put("currentDrift","CurrentDrift");
        reportMapLabels.put("time","time");
        reportMapLabels.put("recalculateDrift","RecalculateDrift");
        reportMapLabels.put("costs","costs");

        reportMapLabels.put("meanRecoveryStatus","mean [ ycor ] of clients + 100");
        reportMapLabels.put("totalClients","TotalClients");
        reportMapLabels.put("exit","TotalClients - count clients");
        reportMapLabels.put("goodExit6Months", "GoodExit6Months");
        reportMapLabels.put("goodExit18Months", "GoodExit18Months");
        reportMapLabels.put("goodExit24Months", "GoodExit24Months");
        reportMapLabels.put("goodExit36Months", "GoodExit36Months");
        reportMapLabels.put("neutralExit36PlusMonths","NeutralExit36PlusMonths");
        reportMapLabels.put("bottom6Mo", "BadExit6Months");
        reportMapLabels.put("bottom18Mo", "BadExit18Months");
        reportMapLabels.put("bottom24Mo", "BadExit24Months");
        reportMapLabels.put("bottom36Mo", "BadExit36Months");
        reportMapLabels.put("bottom36+Mo","BadExit36PlusMonths");

        reportMapLabels.put("Solicitors","Solicitors");
        reportMapLabels.put("NewClients","NewClients");
        reportMapLabels.put("RandomVariation","RandomVariation");



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


        workspace.registerReports(reportMapLabels);
>>>>>>> Stashed changes

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
