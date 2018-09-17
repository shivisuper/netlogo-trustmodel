package com.netlogo.trustmodel.services;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// TODO: Consider moving all this to the trust-model UI in order to make the backend as model agnostic as possible
@Service
public class TrustModelService {
    // TODO: These should really be in a messages.properties and obtained through a org.springframework.context.MessageSource

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

    public Map<String, String> generateReporterSourceMap() {
        val reporterSourceMap = new HashMap<String, String>();

        // Labels initial values
        reporterSourceMap.put("clientsGreater5Yrs_Label", clientsGreater5Yrs);
        reporterSourceMap.put("injurySeverity_Label", injurySeverity);
        reporterSourceMap.put("atFaultStatus_Label", atFaultStatus);
        reporterSourceMap.put("healthStatus_Label", healthStatus);
        reporterSourceMap.put("previousInjury_Label", previousInjury);
        reporterSourceMap.put("embeddedness_Label", embeddedness);
        reporterSourceMap.put("employmentStatus_Label", employmentStatus);
        reporterSourceMap.put("vulnerableStatus_Label", vulnerableStatus);
        reporterSourceMap.put("gender_Label", gender);
        reporterSourceMap.put("age_Label", age);
        reporterSourceMap.put("claimDuration_Label", claimDuration);
        reporterSourceMap.put("injuryClassification_Label", injuryClassification);
        reporterSourceMap.put("education_Label", education);
        reporterSourceMap.put("drift_Label", drift);
        reporterSourceMap.put("waitListEffect_Label", waitListEffect);
        reporterSourceMap.put("driftWaitListEffect_Label", driftWaitListEffect);
        reporterSourceMap.put("currentDrift_Label", currentDrift);
        reporterSourceMap.put("time_Label", "time");
        reporterSourceMap.put("recalculateDrift_Label", label_recalculateDrift);
        reporterSourceMap.put("costs_Label", costs);
        reporterSourceMap.put("meanRecoveryStatus_Label", meanRecoveryStatus);
        reporterSourceMap.put("totalClients_Label", totalClients);
        reporterSourceMap.put("exit_Label", exit);
        reporterSourceMap.put("goodExit6Months_Label", goodExit6Months);
        reporterSourceMap.put("goodExit18Months_Label", goodExit18Months);
        reporterSourceMap.put("goodExit24Months_Label", goodExit24Months);
        reporterSourceMap.put("goodExit36Months_Label", goodExit36Months);
        reporterSourceMap.put("neutralExit36PlusMonths_Label", neutralExit36PlusMonths);
        reporterSourceMap.put("bottom6Mo_Label", bottom6Mo);
        reporterSourceMap.put("bottom18Mo_Label", bottom18Mo);
        reporterSourceMap.put("bottom24Mo_Label", bottom24Mo);
        reporterSourceMap.put("bottom36Mo_Label", bottom36Mo);
        reporterSourceMap.put("bottom36PlusMo_Label", bottom36PlusMo);
        reporterSourceMap.put("commonLawNum_Label", commonLawNum);
        reporterSourceMap.put("countClient_Label", countClient);
        reporterSourceMap.put("commonLawPercent_Label", commonLawPercent);
        reporterSourceMap.put("percentGoodExit_Label", percentGoodExit);
        reporterSourceMap.put("percentBadExit_Label", percentBadExit);

        // Sliders initial values
        reporterSourceMap.put("newClients_Slider", newClients);
        reporterSourceMap.put("roadSafetyEffectiveness_Slider", roadSafetyEffectiveness);
        reporterSourceMap.put("injuryRecovery_Slider", injuryRecovery);
        reporterSourceMap.put("randomVariation_Slider", randomVariation);
        reporterSourceMap.put("recalculateDrift_Slider", slider_recalculateDrift);
        reporterSourceMap.put("shockZone1Starts_Slider", shockZone1Starts);
        reporterSourceMap.put("shockZone1Ends_Slider", shockZone1Ends);
        reporterSourceMap.put("shockZone2Starts_Slider", shockZone2Starts);
        reporterSourceMap.put("shockZone2Ends_Slider", shockZone2Ends);
        reporterSourceMap.put("reliefZone1Starts_Slider", reliefZone1Starts);
        reporterSourceMap.put("reliefZone1Ends_Slider", reliefZone1Ends);
        reporterSourceMap.put("reliefZone2Starts_Slider", reliefZone2Starts);
        reporterSourceMap.put("reliefZone2Ends_Slider", reliefZone2Ends);
        reporterSourceMap.put("shockZone1Increase_Slider", shockZone1Increase);
        reporterSourceMap.put("shockZone2Increase_Slider", shockZone2Increase);
        reporterSourceMap.put("reliefZone1Decrease_Slider", reliefZone1Decrease);
        reporterSourceMap.put("reliefZone2Decrease_Slider", reliefZone2Decrease);
        reporterSourceMap.put("driftModifier_Slider", driftModifier);
        reporterSourceMap.put("solicitors_Slider", solicitors);

        return reporterSourceMap;
    }
}
