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
        reportMap.put("waitlisteffect","Waitlisteffect");
        reportMap.put("totalClients","TotalClients");
        reportMap.put("schemeType","scheme-type");
        reportMap.put("costs","costs");
        reportMap.put("shockZoneSD","ShockZoneSD");
        reportMap.put("currentDrift","CurrentDrift");
        reportMap.put("commonLawCapture", "CommonLawCapture");
        reportMap.put("downwardDriftModifier", "DownwardDriftModifier");
        reportMap.put("upwardDriftModifier", "UpwardDriftModifier");
        reportMap.put("employmentStatus", "mean [ EmploymentStatus ] of clients");
        reportMap.put("vulnerableStatus", "mean [ VulnerableStatus ] of clients");
        reportMap.put("age", "mean [ Age ] of clients");
        reportMap.put("education", "mean [ Education ] of clients");
        reportMap.put("gender", "mean [ Gender ] of clients");
        reportMap.put("injurySeverity", "mean [ InjurySeverity ] of clients");
        reportMap.put("claimDuration", "mean [ ClaimDuration ] of clients");
        reportMap.put("goodExit6Months", "GoodExit6Months");
        reportMap.put("goodExit18Months", "GoodExit18Months");
        reportMap.put("goodExit24Months", "GoodExit24Months");
        reportMap.put("goodExit36Months", "GoodExit36Months");
        reportMap.put("badExit6Months", "BadExit6Months");
        reportMap.put("badExit18Months", "BadExit18Months");
        reportMap.put("badExit24Months", "BadExit24Months");
        reportMap.put("badExit36Months", "BadExit36Months");
        reportMap.put("healthStatus", "mean [ HealthStatus ] of clients");
        reportMap.put("injuryClassification", "mean [ InjuryClassification ] of clients");
        reportMap.put("drift", "mean [ Drift ] of clients");
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
