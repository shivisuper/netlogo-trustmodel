;;; things to do
;  export check points on turtle's life, starting conditions and when exiting the screen (x,y,client properties)
;  record number of issues for each client
;  add in TAC processes, add bands for processes, allow moving to different points in the timeline, number of items in each, width of each
;  find out how starting conditions impact the ending point
;  get to ratio of 80% recover in 6 months, 19% are long term, 1% are lifetime
;  if at fault, do not switch to orange, only not at fault eligible for common law
;  find who exits at 6 months, 18, 24, and 36
;  set up configuable problem zones, have a +X% at around 6 months, then a -X%, then +X% at half way, then a -X% after that
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; TAC Common Law process Agent-Based Model V.1.0 ;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

extensions [ nw palette ]



globals [
  Waitlisteffect ;; average amount people improve per week
  CurrentDrift
  time
  InjuryRecoverySD
  GoodExit6Months
  GoodExit18Months
  GoodExit24Months
  GoodExit36Months
  GoodExit36PlusMonths
  BadExit6Months
  BadExit18Months
  BadExit24Months
  BadExit36Months
  BadExit36PlusMonths
  NeutralExit36PlusMonths
  DownDriftFactor1
  DownDriftFactor2
  DownDriftFactor3
  UpDriftFactor1
  UpDriftFactor2
  UpDriftFactor3
  CommonLawCapture
  RandomCapture
  DownwardDriftModifier
  UpwardDriftModifier

TurtleArchiveClientArchiveWho
TurtleArchiveColor
TurtleArchiveHeading
TurtleArchiveXcor
TurtleArchiveYcor
TurtleArchiveHidden
TurtleArchiveHealthstatus
TurtleArchiveInjurystatus
TurtleArchiveAtfaultstatus
TurtleArchivePreviousinjury
TurtleArchiveDrift
TurtleArchiveEmbeddedness
TurtleArchiveEmployementstatus
TurtleArchiveVulnerablestatus
TurtleArchiveGender
TurtleArchiveAge
TurtleArchiveClaimduration
TurtleArchiveInjuryclassification
TurtleArchiveEducation
TurtleArchiveStartinghealthstatus
TurtleArchiveIssuesencountered
TurtleArchiveRockbottom

  TotalClients
  scheme-type
  costs
  ShockZoneSD


]

breed [
  clients client
]

;breed
;  [ issues issue
;]

clients-own [
  HealthStatus ;;
  InjurySeverity ;;
  AtFaultStatus ;; (1 not responsible, 3 totally responsible)
  PreviousInjury ;; Have they had a previous injury?
  Drift ;; sum of all risk factors
  ;SixMonthStatus ;;  Mean time it takes for people to exit
  Embeddedness ;;
  EmploymentStatus ;; 0-not employed, 1-employed
  VulnerableStatus ;; 0-not vulnerable, 1-vulnerable
  Gender ;; 0 Female, 1 Male
  Age
  ClaimDuration ;; 0 (0-12 months), 3 (37-72 months)
  InjuryClassification ;; 0=Muscularskeletal, 3=other severe
  Education ;; 1=primary school, 10=postgradute
  EducationWeight
  GenderWeight
  AgeWeight
  VulnerableStatusWeight
  EmploymentStatusWeight ;; 0-not employed, 1-employed, weight of 0.11
  AtFaultStatusWeight ;; personal responsibility for accident: (1 not responsible, 3 totally responsible), a weight of -0.35
  ClaimDurationWeight
  InjuryClassificationWeight
  IssuesEncountered
  StartingHealthStatus
  RockBottom
  ClientCost
  ]

patches-own [
  drag
  trust
]

;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; SETUP PROCEDURES ;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

to setup
  clear-all
  setup-globals
  setup-clients
  setup-patches
  reset-ticks
end

to setup-globals
  set InjuryRecoverySD 5
  ;set InjuryRecovery 60
  set GoodExit6Months 0
  set GoodExit18Months 0
  set GoodExit24Months 0
  set GoodExit36Months 0
  set BadExit6Months 0
  set BadExit18Months 0
  set BadExit24Months 0
  set BadExit36Months 0
  set BadExit36PlusMonths 0
  set NeutralExit36PlusMonths 0
  set DownDriftFactor1 1
  set DownDriftFactor2 1
  set DownDriftFactor3 1
  set UpDriftFactor1 1
  set UpDriftFactor2 1
  set UpDriftFactor3 1
  set CommonLawCapture 0
  set DownwardDriftModifier 4
  set UpwardDriftModifier 3

  set TurtleArchiveClientArchiveWho []
  set TurtleArchiveColor []
  set TurtleArchiveHeading []
  set TurtleArchiveXcor []
  set TurtleArchiveYcor []
  set TurtleArchiveHidden []
  set TurtleArchiveHealthstatus []
  set TurtleArchiveInjurystatus []
  set TurtleArchiveAtfaultstatus []
  set TurtleArchivePreviousinjury []
  set TurtleArchiveDrift []
  set TurtleArchiveEmbeddedness []
  set TurtleArchiveEmployementstatus []
  set TurtleArchiveVulnerablestatus []
  set TurtleArchiveGender []
  set TurtleArchiveAge []
  set TurtleArchiveClaimduration []
  set TurtleArchiveInjuryclassification []
  set TurtleArchiveEducation []
  set TurtleArchiveStartinghealthstatus []
  set TurtleArchiveIssuesencountered []
  set TurtleArchiveRockbottom []
  set TotalClients 0
  set costs 0
  set ShockZoneSD 30
end

to setup-clients
    if  100 - Road_Safety_Effectiveness > random 100 [ create-clients 1 [

    set IssuesEncountered 0
    set Embeddedness random-normal 0 20

    set InjurySeverity random-normal 50 10
    set HealthStatus random-normal 50 10
    set StartingHealthStatus HealthStatus
    set PreviousInjury random-normal 50 10
    ;set SixMonthStatus ( (xcor + 300) / random-normal 180 180 ) * 10
    ;set SixMonthStatus 1

    set AtFaultStatus random 3
    if AtFaultStatus = 0
      [set AtFaultStatusWeight 1 + (0.35 / 2)]
    if AtFaultStatus = 1
      [set AtFaultStatusWeight 1 ]
    if AtFaultStatus = 2
      [set AtFaultStatusWeight 1 - (0.35 / 2)]

    set EmploymentStatus random 2
    if EmploymentStatus = 0
      [set EmploymentStatusWeight 1 ]
    if EmploymentStatus = 1
      [set EmploymentStatusWeight 1 + 0.11 ]

    set VulnerableStatus random 2
    if VulnerableStatus = 0
      [set VulnerableStatusWeight 1 ]
    if VulnerableStatus = 1
      [set VulnerableStatusWeight 1 + 0.17 ]

    set Age (random-float 72 )  + 16  ;; to get range between 16 and 88
    set AgeWeight 0.915 + ((Age - 16) * .0023611)

    set Gender random 2
    if Gender = 0
      [set GenderWeight 1 - 0.10]
    if Gender = 1
      [set GenderWeight 1 + 0.12]

    set ClaimDuration random 4
    if ClaimDuration = 0
      [set ClaimDurationWeight 1 + 0.05 ]
    if ClaimDuration = 1
      [set ClaimDurationWeight 1 + 0.025  ]
    if ClaimDuration = 2
      [set ClaimDurationWeight 1 - 0.025  ]
    if ClaimDuration = 3
      [set ClaimDurationWeight 1 - 0.05 ]

    set InjuryClassification random 4
    if InjuryClassification = 0
      [set InjuryClassificationWeight 1 - 0.05 ]
    if InjuryClassification = 1
      [set InjuryClassificationWeight 1 - 0.025  ]
    if InjuryClassification = 2
      [set InjuryClassificationWeight 1 + 0.025  ]
    if InjuryClassification = 3
      [set InjuryClassificationWeight 1 + 0.05 ]

    set Education random 11
    set EducationWeight 0.95 + (Education * 0.1 / 11)

    set Drift driftrecalulate HealthStatus InjurySeverity PreviousInjury AtFaultStatusWeight EmploymentStatusWeight VulnerableStatusWeight GenderWeight AgeWeight ClaimDurationWeight InjuryClassificationWeight EducationWeight

    ;set Drift (   (HealthStatus / 50 -  InjurySeverity / 50 - PreviousInjury / 50 )
    ;  + (AtFaultStatusWeight  )
    ;  + (EmploymentStatusWeight )
    ;  + (VulnerableStatusWeight )
    ;  + GenderWeight
    ;  + AgeWeight
    ;  + (ClaimDurationWeight)
    ;  + (InjuryClassificationWeight)
    ;  + (EducationWeight)
    ;  )/ 11 * 50
    set CurrentDrift Drift
    set waitlisteffect random-normal InjuryRecovery InjuryRecoverySD
    set xcor -300
    set ycor Embeddedness
    set shape "circle"
    set color white
    set size 3
    set RockBottom 0
    set TotalClients TotalClients + 1
    set ClientCost 0

    ]
  ]
  end

to setup-patches

  let baseDrag 55
  ask patches
  [
    let aveDrag 50 ;; has a mean of 50 and sd of (10+pycor/10)
    let sdDrag 30 - pycor / 10
    ifelse pycor < -75
    [
      set aveDrag baseDrag
      set sdDrag 10 - pycor / 10
    ]
    [
      ifelse pycor < -50
      [
        set aveDrag baseDrag * 0.95
        set sdDrag 10 - pycor / 10
      ]
      [
        ifelse pycor < -25
        [
          set aveDrag baseDrag * 0.90
          set sdDrag 10 - pycor / 10
        ]
        [
          ifelse pycor < 0
          [
            set aveDrag baseDrag * 0.85
            set sdDrag 10 - pycor / 10
          ]
          [
            ifelse pycor < 25
            [
              set aveDrag baseDrag * 0.75
              set sdDrag 10 - pycor / 10
            ]
            [
              ifelse pycor < 25
              [
                set aveDrag baseDrag * 0.65
                set sdDrag 10 - pycor / 10
              ]
              [
                ifelse pycor < 25
                [
                  set aveDrag baseDrag * 0.60
                  set sdDrag 10 - pycor / 10
                ]
                [
                   set aveDrag baseDrag * 0.5
                   set sdDrag 10 - pycor / 10
                ]
              ]
            ]
          ]
        ]
      ]
    ]

    set pcolor 104
    set drag random-normal aveDrag sdDrag
    shockarea1

    ;set pcolor palette:scale-gradient [[255 0 0] [0 0 255]] drag 5 50
    ;set pcolor palette:scale-gradient palette:scheme-colors "Divergent" "Spectral" 10 drag 200 0

    createtrouble
    if pycor = 0 [set pcolor grey]
  ]
end

to reset-shock
  ;ask issues
  ;[ die ]
  clear-patches
  ;ask patches [
  ;  set drag random-normal 50 10 - pycor / 10 ;; has a mean of 50 and sd of (10+pycor/10)
  ;  set pcolor 104
  ;  shockarea1
  ;  createtrouble
  ;]
  setup-patches
end

;;;;;;;;;;;;;;;;;;;;;;;
;;;; GO PROCEDURES ;;;;
;;;;;;;;;;;;;;;;;;;;;;;

to go
  ask clients [
    progress
    exitscheme
    status
    hump
    boost
        ]
  launchnewclients
  ask patches [
    changepatchcolor
         ]
  catchclients
   tick
  set time time + 1
end

;to progress
;   let h [ drag ] of patch-here + ( drift ) - waitlisteffect
;    ifelse h > 0 [ set heading 180 fd 1 ] ; this sends you upward towards bad things (now reversed to down)
;      [ set heading 0 fd 1 ] ;this sends you downward towards good things (now reversed to up)
;    ifelse hidden? = true  [set heading 90 fd 0  ]  ;; don't move hidden turtles
;      [set heading 90 fd 1 ]  ;this moves you one forward across the days
;end

;; ycor will be -100 to 100. Scale that to a cost of 0 for 100, 1 for 0, and 2 for -100

;;;   function scaleBetween(unscaledNum, minAllowed, maxAllowed, min, max) {
;;;  return (maxAllowed - minAllowed) * (unscaledNum - min) / (max - min) + minAllowed;
;;;  }
to-report getCost[ycoordinate]
  let minAllowed 2
  let maxAllowed 0
  let minRange -100
  let maxRange 100
  let rescaledCost (maxAllowed - minAllowed) * (ycoordinate - minRange) / (maxRange - minRange) + minAllowed
  ;print (word "There are " rescaledCost " turtles." ycoordinate " " )

  report rescaledCost
end

to progress
  ifelse hidden? = true []  ;; don't move hidden turtles
  [
    ifelse xcor > 0
    [   ;; this case is future functionality. Once you are past 3 years, proceed across at your y level unless you have interactions with laywers, etc
      ;set heading 90 fd 1   ;this moves you one forward across the days

      let h [ drag ] of patch-here + ( drift ) - waitlisteffect
      ifelse h > 0
        [ set heading 180 fd calcDownModifier xcor DownwardDriftModifier
          set IssuesEncountered IssuesEncountered + 1
        ] ; this sends you upward towards bad things (now reversed to down)
        [ set heading 0 fd calcUpModifier xcor UpwardDriftModifier

        ] ;this sends you downward towards good things (now reversed to up)
      set heading 90 fd 1   ;this moves you one forward across the days

    ]
    [
     let h [ drag ] of patch-here + ( drift ) - waitlisteffect
     ifelse h > 0
       [
         set heading 180 fd calcDownModifier xcor DownwardDriftModifier
         set IssuesEncountered IssuesEncountered + 1
       ] ; this sends you upward towards bad things (now reversed to down)
       [
         set heading 0 fd calcUpModifier xcor UpwardDriftModifier
       ] ;this sends you downward towards good things (now reversed to up)
     set heading 90 fd 1   ;this moves you one forward across the days
    ]
    set costs costs + getCost ycor
    set ClientCost ClientCost + getCost ycor
  ]
end


to-report calcUpModifier [xvalue driftmod]
  ;set returnValue 0
  ifelse xvalue < -250 [report UpDriftFactor1]
    [   ifelse xvalue < 0 [report UpDriftFactor2]
      [report UpDriftFactor3]
    ]
  ;report returnValue
end

to-report calcDownModifier [xvalue driftmod]

    ifelse xvalue < -250 [report DownDriftFactor1]
    [   ifelse xvalue < 0 [report DownDriftFactor2]
      [report DownDriftFactor3]
    ]

  ;set returnValue 0
;  ifelse xvalue < -250 [report 1 * driftmod]
;    [ report 1 ]
  ;report returnValue
end

to exitscheme
  ask clients [
    if ycor > 99 and hidden? = false
    [
;      set TurtleArchiveClientArchiveWho lput who TurtleArchiveClientArchiveWho
;      set TurtleArchiveColor lput color TurtleArchiveColor
;      set TurtleArchiveHeading lput heading TurtleArchiveHeading
;      set TurtleArchiveXcor  lput xcor TurtleArchiveXcor
;      set TurtleArchiveYcor  lput ycor TurtleArchiveYcor
;      set TurtleArchiveHidden  lput hidden? TurtleArchiveHidden
;      set TurtleArchiveHealthstatus  lput HealthStatus TurtleArchiveHealthstatus
;      set TurtleArchiveInjurystatus  lput InjurySeverity  TurtleArchiveInjurystatus
;      set TurtleArchiveAtfaultstatus  lput AtFaultStatus TurtleArchiveAtfaultstatus
;      set TurtleArchivePreviousinjury  lput PreviousInjury TurtleArchivePreviousinjury
;      set TurtleArchiveDrift  lput Drift TurtleArchiveDrift
;      set TurtleArchiveEmbeddedness  lput Embeddedness TurtleArchiveEmbeddedness
;      set TurtleArchiveEmployementstatus lput EmploymentStatus TurtleArchiveEmployementstatus
;      set TurtleArchiveVulnerablestatus  lput VulnerableStatus TurtleArchiveVulnerablestatus
;      set TurtleArchiveGender  lput Gender TurtleArchiveGender
;      set TurtleArchiveAge lput Age TurtleArchiveAge
;      set TurtleArchiveClaimduration lput ClaimDuration TurtleArchiveClaimduration
;      set TurtleArchiveInjuryclassification lput InjuryClassification TurtleArchiveInjuryclassification
;      set TurtleArchiveEducation lput Education TurtleArchiveEducation
;      set TurtleArchiveStartinghealthstatus lput StartingHealthStatus TurtleArchiveStartinghealthstatus
;      set TurtleArchiveIssuesencountered  lput IssuesEncountered TurtleArchiveIssuesencountered
;      set TurtleArchiveRockbottom lput RockBottom TurtleArchiveRockbottom


         ;;hide them so that they don't disappear from the saved dataset
      ifelse xcor <= -250 [ set GoodExit6Months GoodExit6Months + 1]
        [ifelse xcor <= -150 [ set GoodExit18Months GoodExit18Months + 1]
          [
             ifelse xcor <= -100 [ set GoodExit24Months GoodExit24Months + 1]
              [ifelse xcor <= 0 [ set GoodExit36Months GoodExit36Months + 1]
                  [set GoodExit36PlusMonths GoodExit36PlusMonths + 1]
              ]
          ]
      ]
      die
    ]
    if xcor > 299 and hidden? = false  ;; what to do in this case? They are in neutral territory but haven't had a good or bad exit
    [ ;ht
      set NeutralExit36PlusMonths  NeutralExit36PlusMonths + 1

;      set TurtleArchiveClientArchiveWho lput who TurtleArchiveClientArchiveWho
;      set TurtleArchiveColor lput color TurtleArchiveColor
;      set TurtleArchiveHeading lput heading TurtleArchiveHeading
;      set TurtleArchiveXcor  lput xcor TurtleArchiveXcor
;      set TurtleArchiveYcor  lput ycor TurtleArchiveYcor
;      set TurtleArchiveHidden  lput hidden? TurtleArchiveHidden
;      set TurtleArchiveHealthstatus  lput HealthStatus TurtleArchiveHealthstatus
;      set TurtleArchiveInjurystatus  lput InjurySeverity  TurtleArchiveInjurystatus
;      set TurtleArchiveAtfaultstatus  lput AtFaultStatus TurtleArchiveAtfaultstatus
;      set TurtleArchivePreviousinjury  lput PreviousInjury TurtleArchivePreviousinjury
;      set TurtleArchiveDrift  lput Drift TurtleArchiveDrift
;      set TurtleArchiveEmbeddedness  lput Embeddedness TurtleArchiveEmbeddedness
;      set TurtleArchiveEmployementstatus lput EmploymentStatus TurtleArchiveEmployementstatus
;      set TurtleArchiveVulnerablestatus  lput VulnerableStatus TurtleArchiveVulnerablestatus
;      set TurtleArchiveGender  lput Gender TurtleArchiveGender
;      set TurtleArchiveAge lput Age TurtleArchiveAge
;      set TurtleArchiveClaimduration lput ClaimDuration TurtleArchiveClaimduration
;      set TurtleArchiveInjuryclassification lput InjuryClassification TurtleArchiveInjuryclassification
;      set TurtleArchiveEducation lput Education TurtleArchiveEducation
;      set TurtleArchiveStartinghealthstatus lput StartingHealthStatus TurtleArchiveStartinghealthstatus
;      set TurtleArchiveIssuesencountered  lput IssuesEncountered TurtleArchiveIssuesencountered
;      set TurtleArchiveRockbottom lput RockBottom TurtleArchiveRockbottom

      die
    ]

;    if ycor < -99 and hidden? = false  ;; going to assume that if you get here, then you exit through common law
;    [ ht   ;;hide them so that they don't disappear from the saved dataset
;      ifelse xcor <= -250 [ set BadExit6Months BadExit6Months + 1]
;        [ifelse xcor <= -150 [ set BadExit18Months BadExit18Months + 1]
;          [
;             ifelse xcor <= -100 [ set BadExit24Months BadExit24Months + 1]
;              [ifelse xcor <= 0 [ set BadExit36Months BadExit36Months + 1]
;                  [set BadExit36PlusMonths BadExit36PlusMonths + 1]
;              ]
;          ]
;      ]
;    ]

    if ycor < -99 and hidden? = false  ;; if they get here, and it is past 18 months, then see if they get caught by common law
    [
      if xcor > -150 and AtFaultStatus < 3 ;can't use common law if at fault
      [ ;; there are 300 + 150 timesteps. If we want a 25% chance of capture over 450 timesteps (450*4*Solicitors)
         ;; get a random number between 0 and 900000 (for 50 Solicitors), if it is less than the number of Solicitors, then capture it for common law
        set RandomCapture random-float 450 * 4 * Solicitors * 10 * count Clients with [color = orange and hidden? = false]
         if RandomCapture < Solicitors
         [
          ;ht
          set CommonLawCapture CommonLawCapture + 1

;          set TurtleArchiveClientArchiveWho lput who TurtleArchiveClientArchiveWho
;          set TurtleArchiveColor lput color TurtleArchiveColor
;          set TurtleArchiveHeading lput heading TurtleArchiveHeading
;          set TurtleArchiveXcor  lput xcor TurtleArchiveXcor
;          set TurtleArchiveYcor  lput ycor TurtleArchiveYcor
;          set TurtleArchiveHidden  lput hidden? TurtleArchiveHidden
;          set TurtleArchiveHealthstatus  lput HealthStatus TurtleArchiveHealthstatus
;          set TurtleArchiveInjurystatus  lput InjurySeverity  TurtleArchiveInjurystatus
;          set TurtleArchiveAtfaultstatus  lput AtFaultStatus TurtleArchiveAtfaultstatus
;          set TurtleArchivePreviousinjury  lput PreviousInjury TurtleArchivePreviousinjury
;          set TurtleArchiveDrift  lput Drift TurtleArchiveDrift
;          set TurtleArchiveEmbeddedness  lput Embeddedness TurtleArchiveEmbeddedness
;          set TurtleArchiveEmployementstatus lput EmploymentStatus TurtleArchiveEmployementstatus
;          set TurtleArchiveVulnerablestatus  lput VulnerableStatus TurtleArchiveVulnerablestatus
;          set TurtleArchiveGender  lput Gender TurtleArchiveGender
;          set TurtleArchiveAge lput Age TurtleArchiveAge
;          set TurtleArchiveClaimduration lput ClaimDuration TurtleArchiveClaimduration
;          set TurtleArchiveInjuryclassification lput InjuryClassification TurtleArchiveInjuryclassification
;          set TurtleArchiveEducation lput Education TurtleArchiveEducation
;          set TurtleArchiveStartinghealthstatus lput StartingHealthStatus TurtleArchiveStartinghealthstatus
;          set TurtleArchiveIssuesencountered  lput IssuesEncountered TurtleArchiveIssuesencountered
;          set TurtleArchiveRockbottom lput RockBottom TurtleArchiveRockbottom

          die
         ]
      ]
      ;;ht   ;;hide them so that they don't disappear from the saved dataset
      ;ifelse xcor <= -250 [ set BadExit6Months BadExit6Months + 1]
      ;  [ifelse xcor <= -150 [ set BadExit18Months BadExit18Months + 1]
      ;    [
      ;       ifelse xcor <= -100 [ set BadExit24Months BadExit24Months + 1]
      ;        [ifelse xcor <= 0 [ set BadExit36Months BadExit36Months + 1]
      ;            [set BadExit36PlusMonths BadExit36PlusMonths + 1]
      ;        ]
      ;    ]
      ;]
    ]

  ]
end


to launchnewclients
  setup-clients
end

to save-world-state
     ;export-world "world.csv"
     nw:set-context turtles links
     nw:save-graphml "turtles.graphml"
     export-plot "Client paths" "ClientPaths.csv"
end

to wave
    create-clients NewClients [
    set Embeddedness random-normal 0 20

    set InjurySeverity random-normal 50 10
    set HealthStatus random-normal 50 10
    set StartingHealthStatus HealthStatus
    set PreviousInjury random-normal 50 10
    ;set SixMonthStatus ( (xcor + 300) / random-normal 180 180 ) * 10
    ;set SixMonthStatus 1

    set AtFaultStatus random 3
    if AtFaultStatus = 0
      [set AtFaultStatusWeight 1 + (0.35 / 2)]
    if AtFaultStatus = 1
      [set AtFaultStatusWeight 1 ]
    if AtFaultStatus = 2
      [set AtFaultStatusWeight 1 - (0.35 / 2)]

    set EmploymentStatus random 2
    if EmploymentStatus = 0
      [set EmploymentStatusWeight 1 ]
    if EmploymentStatus = 1
      [set EmploymentStatusWeight 1 + 0.11 ]

    set VulnerableStatus random 2
    if VulnerableStatus = 0
      [set VulnerableStatusWeight 1 ]
    if VulnerableStatus = 1
      [set VulnerableStatusWeight 1 + 0.17 ]

    set Age (random-float 72 )  + 16  ;; to get range between 16 and 88
    set AgeWeight 0.915 + ((Age - 16) * .0023611)

    set Gender random 2
    if Gender = 0
      [set GenderWeight 1 - 0.10]
    if Gender = 1
      [set GenderWeight 1 + 0.12]

    set ClaimDuration random 4
    if ClaimDuration = 0
      [set ClaimDurationWeight 1 + 0.05 ]
    if ClaimDuration = 1
      [set ClaimDurationWeight 1 + 0.025  ]
    if ClaimDuration = 2
      [set ClaimDurationWeight 1 - 0.025  ]
    if ClaimDuration = 3
      [set ClaimDurationWeight 1 - 0.05 ]

    set InjuryClassification random 4
    if InjuryClassification = 0
      [set InjuryClassificationWeight 1 - 0.05 ]
    if InjuryClassification = 1
      [set InjuryClassificationWeight 1 - 0.025  ]
    if InjuryClassification = 2
      [set InjuryClassificationWeight 1 + 0.025  ]
    if InjuryClassification = 3
      [set InjuryClassificationWeight 1 + 0.05 ]

    set Education random 11
    set EducationWeight 0.95 + (Education * 0.1 / 11)

    set Drift driftrecalulate HealthStatus InjurySeverity PreviousInjury AtFaultStatusWeight EmploymentStatusWeight VulnerableStatusWeight GenderWeight AgeWeight ClaimDurationWeight InjuryClassificationWeight EducationWeight
    set waitlisteffect random-normal InjuryRecovery InjuryRecoverySD
    set xcor -300
    set ycor Embeddedness
    set shape "circle"
    set color white
    set size 3 ]
end

to changepatchcolor
;  if any? clients-here
;  [set pcolor 9.9  ]
;   if 100 > random 50000
;  [set pcolor palette:scale-gradient palette:scheme-colors "Divergent" "Spectral" 10 drag 5 40]
end


to changepatchcolor-old
  if any? clients-here
 ; [set pcolor pcolor * 1.01]
  ;;;[set pcolor ( list ( 255 - patch-value ) 0 patch-value ) ]
  [set pcolor 9.9  ]
;   if pcolor > 109 [ set pcolor 109 ]
;   if pcolor < 104 [ set pcolor 104 ]
   if 100 > random 50000
  ;[ set pcolor pcolor * .99 ]
  [set pcolor palette:scale-gradient palette:scheme-colors "Divergent" "Spectral" 10 drag 5 40]
end

to status
  if ycor > 75 [ set color green ]
  if ycor < -75 and ycor > -99 [ set color orange ]
  if ycor < -99
  [set color red

    if ycor < -99 and hidden? = false and RockBottom = 0
    [
      ifelse xcor <= -250 [ set BadExit6Months BadExit6Months + 1]
        [ifelse xcor <= -150 [ set BadExit18Months BadExit18Months + 1]
          [
             ifelse xcor <= -100 [ set BadExit24Months BadExit24Months + 1]
              [
                ifelse xcor <= 0 [ set BadExit36Months BadExit36Months + 1]
                  [set BadExit36PlusMonths BadExit36PlusMonths + 1]
              ]
          ]
       ]
    ]
    set RockBottom 1

  ]
end

to shockarea1
  if pxcor > ShockZone2Starts and pxcor < ShockZone2Ends
  [
    set drag drag + random-normal ShockZone2Increase ShockZoneSD
    set pcolor red
  ]
  if pxcor > ShockZone1Starts and pxcor < ShockZone1Ends
  [
    set drag drag + random-normal ShockZone1Increase ShockZoneSD
    set pcolor red
  ]

  if pxcor > ReliefZone2Starts and pxcor < ReliefZone2Ends
  [
    set drag drag - random-normal ReliefZone1Decrease ShockZoneSD
    set pcolor green
  ]
  if pxcor > ReliefZone1Starts and pxcor < ReliefZone1Ends
  [
    set drag drag - random-normal ReliefZone2Decrease ShockZoneSD
    set pcolor green
  ]

end

to createtrouble
  if (pxcor > ShockZone2Starts and pxcor < ShockZone2Ends) or (pxcor > ShockZone1Starts and pxcor < ShockZone1Ends)
  [
;    if drag > 75 and not any? issues-here [ sprout-issues 1 ]
  ]
end

to hump
  ;set SixMonthStatus ( (xcor + 300) / 90 ) * 10
end

to catchclients
;  ask issues
;  [ set heading one-of [ 180 0 ] fd 1 ]
end


to-report driftrecalulate [hts ij pr at em vu ge ag cl ic ed]
  report (   (hts / 50 -  ij / 50 - pr / 50 )
      + (at  )
      + (em )
      + (vu )
      + ge
      + ag
      + (cl)
      + (ic)
      + (ed)
      )/ 11 * 50
end

to boost
  ;if any? issues-here
  ;  [
      set Drift Drift * DriftModifier
     ; set IssuesEncountered IssuesEncountered  + 1
       if RecalculateDrift
      [ set HealthStatus 50 + (ycor / 10)  ;; at an issue, use current ycor location to recalibrate healthstatus before recalculating drift
        set Drift driftrecalulate HealthStatus InjurySeverity PreviousInjury AtFaultStatusWeight EmploymentStatusWeight VulnerableStatusWeight GenderWeight AgeWeight ClaimDurationWeight InjuryClassificationWeight EducationWeight]
 ; ]
end

;to boost
;  if any? issues-here
;    [ set Drift Drift * DriftModifier
;      set IssuesEncountered IssuesEncountered  + 1
;       if RecalculateDrift
;      [ set HealthStatus 50 + (ycor / 10)  ;; at an issue, use current ycor location to recalibrate healthstatus before recalculating drift
;        set Drift driftrecalulate HealthStatus InjurySeverity PreviousInjury AtFaultStatusWeight EmploymentStatusWeight VulnerableStatusWeight GenderWeight AgeWeight ClaimDurationWeight InjuryClassificationWeight EducationWeight]
;  ]
;end
@#$#@#$#@
GRAPHICS-WINDOW
301
61
1342
415
-1
-1
1.72
1
10
1
1
1
0
1
0
1
-300
300
-100
100
0
0
1
days
30.0

BUTTON
5
66
88
99
setup
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
92
66
175
99
go
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
0

SLIDER
16
135
189
168
NewClients
NewClients
0
1000
20.0
5
1
NIL
HORIZONTAL

TEXTBOX
833
15
957
33
NIL
11
0.0
1

TEXTBOX
84
36
170
54
Main commands
11
0.0
1

TEXTBOX
49
104
223
122
Incoming Client Controls
11
0.0
1

PLOT
250
459
774
650
Client Status Charts
NIL
NIL
0.0
10.0
0.0
10.0
true
true
"" ";;if ticks = 200 [ reset ] "
PENS
"Health" 1.0 0 -7500403 true "" "plot mean [ ycor ] of clients * 10"
"5 years" 1.0 0 -2674135 true "" "plot count clients with [ xcor > 250 ]"
"6 months" 1.0 0 -955883 true "" "plot count clients with [ xcor > -250 ] "
"3 years + " 1.0 0 -6459832 true "" "plot count clients with [ xcor > 50 ]"

SLIDER
60
425
232
458
InjuryRecovery
InjuryRecovery
0
100
85.0
1
1
NIL
HORIZONTAL

PLOT
47
276
247
396
Client Health Status
NIL
NIL
0.0
10.0
0.0
10.0
true
false
"" ""
PENS
"default" 1.0 0 -2674135 true "" "plot mean [ HealthStatus ] of clients"

SLIDER
13
186
226
219
Road_Safety_Effectiveness
Road_Safety_Effectiveness
1
100
5.0
1
1
NIL
HORIZONTAL

BUTTON
178
66
257
99
Go Once
Go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SLIDER
37
502
209
535
RandomVariation
RandomVariation
0
.2
0.171
.001
1
NIL
HORIZONTAL

MONITOR
1197
561
1318
606
Exit
TotalClients - count clients
0
1
11

BUTTON
1362
171
1509
205
PreviousInjury +
ask clients [ set PreviousInjury PreviousInjury + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1510
171
1654
205
PreviousInjury - 
ask clients [ set PreviousInjury PreviousInjury - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1381
61
1505
95
InjurySeverity +
ask clients [ set InjurySeverity InjurySeverity + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1508
61
1631
95
InjurySeverity - 
ask clients [ set InjurySeverity InjurySeverity - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1378
98
1506
132
At Fault Status +
ask clients [ set AtFaultStatus AtFaultStatus + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1509
98
1635
132
At Fault Status - 
ask clients [ set AtFaultStatus AtFaultStatus - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1387
135
1506
169
Health Status +
ask clients [ set HealthStatus HealthStatus + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1510
135
1628
169
Health Status - 
ask clients [ set HealthStatus HealthStatus - 1 ]
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

TEXTBOX
1425
23
1623
51
Recovery Risk Modifiers
11
0.0
1

TEXTBOX
63
401
242
429
Recovery Dynamics
11
0.0
1

PLOT
796
460
1117
649
Personal Moderators
NIL
NIL
0.0
10.0
0.0
2.0
true
true
"" ""
PENS
"HealthStatus" 1.0 0 -16777216 true "" "plot mean [ HealthStatus ] of clients * 10 "
"At-Fault Status" 1.0 0 -7500403 true "" "plot count clients with [ AtFaultStatus = 2 ]"

MONITOR
1183
459
1327
504
Mean Recovery Status
mean [ ycor ] of clients + 100
2
1
11

PLOT
1355
495
1721
652
Upper & Lower Limits of Client Health
NIL
NIL
5.0
10.0
0.0
100.0
true
false
"" ""
PENS
"Healthy" 1.0 0 -14439633 true "" "plot count clients with [ color = green ] "
"pen-1" 1.0 0 -955883 true "" "plot count clients with [ color = orange ] "

MONITOR
1235
10
1343
55
Clients > 5 years
count clients with [ xcor > 250 ]
0
1
11

MONITOR
1198
511
1320
556
Total Clients
TotalClients
0
1
11

BUTTON
192
136
256
169
Wave
wave
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

TEXTBOX
38
221
205
239
Less                        More
11
0.0
1

TEXTBOX
304
421
454
439
Date of Accident
11
0.0
1

TEXTBOX
787
418
830
436
3 years
11
0.0
1

TEXTBOX
1301
419
1339
437
6 years
11
0.0
1

TEXTBOX
528
419
678
437
1.5 years
11
0.0
1

TEXTBOX
1063
420
1213
438
4.5 years
11
0.0
1

PLOT
53
653
1111
893
Client paths
NIL
NIL
-300.0
300.0
-100.0
100.0
true
false
"" ""
PENS
"default" 1.0 2 -16777216 true "" "ask clients [\n  create-temporary-plot-pen (word who)\n  plotxy xcor ycor\n]"
"axis" 1.0 0 -7500403 true "" ";; we don't want the \"auto-plot\" feature to cause the\n;; plot's x range to grow when we draw the axis.  so\n;; first we turn auto-plot off temporarily\nauto-plot-off\n;; now we draw an axis by drawing a line from the origin...\nplotxy -300 0\n;; ...to a point that's way, way, way off to the right.\nplotxy 1000000000 0\n;; now that we're done drawing the axis, we can turn\n;; auto-plot back on again\nauto-plot-on"

MONITOR
1657
10
1759
55
InjurySeverity
mean [ InjurySeverity ] of clients
3
1
11

MONITOR
1763
10
1869
55
AtFaultStatus
mean [ AtFaultStatus ] of clients
3
1
11

MONITOR
1656
57
1765
102
HealthStatus
mean [ HealthStatus ] of clients
3
1
11

MONITOR
1767
58
1872
103
PreviousInjury
mean [ PreviousInjury ] of clients
3
1
11

MONITOR
1772
104
1886
149
Embeddedness
mean [ Embeddedness ] of clients
3
1
11

MONITOR
1657
149
1772
194
EmploymentStatus
mean [ EmploymentStatus ] of clients
2
1
11

MONITOR
1772
148
1878
193
VulnerableStatus
mean [ VulnerableStatus ] of clients
2
1
11

MONITOR
1676
195
1768
240
Gender
mean [ Gender ] of clients
2
1
11

MONITOR
1775
193
1876
238
Age
mean [ Age ] of clients
2
1
11

MONITOR
1673
240
1768
285
ClaimDuration
mean [ ClaimDuration ] of clients
2
1
11

MONITOR
1773
241
1872
286
InjuryClassification
mean [ InjuryClassification ] of clients
2
1
11

MONITOR
1692
286
1769
331
Education
mean [ Education ] of clients
2
1
11

BUTTON
1377
243
1512
276
Employment +
ask clients [ set EmploymentStatus EmploymentStatus + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1514
242
1656
275
Employment - 1
ask clients [ set EmploymentStatus EmploymentStatus - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1347
277
1513
310
VulnerableStatus + 1
ask clients [ set VulnerableStatus VulnerableStatus + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1516
276
1662
309
VulnerableStatus - 1
ask clients [ set VulnerableStatus VulnerableStatus + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1363
310
1514
343
Gender + 1
ask clients [ set Gender Gender + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1515
310
1623
343
Gender - 1
ask clients [ set Gender Gender - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1426
345
1515
378
Age + 1
ask clients [ set Age Age + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1518
343
1600
376
Age - 1
ask clients [ set Age Age - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1518
376
1673
409
ClaimDuration - 1
ask clients [ set ClaimDuration ClaimDuration - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1363
445
1522
478
Education + 1
ask clients [ set Education Education + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1521
410
1686
443
InjuryClassification - 1
ask clients [ set InjuryClassification InjuryClassification - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1357
411
1519
444
InjuryClassification + 1
ask clients [ set InjuryClassification InjuryClassification + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1382
377
1517
410
ClaimDuration + 1
ask clients [ set ClaimDuration ClaimDuration + 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
1524
443
1683
476
Education - 1
ask clients [ set Education Education - 1 ] 
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

MONITOR
1706
412
1763
457
Drift
mean [ Drift ] of clients
2
1
11

MONITOR
1780
413
1876
458
Waitlisteffect
mean [ Waitlisteffect ] of clients
2
1
11

MONITOR
1736
473
1878
518
Drift - Waitlisteffect 
mean [ Drift - Waitlisteffect ] of clients
2
1
11

MONITOR
1737
524
1847
569
CurrentDrift
CurrentDrift
2
1
11

MONITOR
1741
576
1798
621
time
time
1
1
11

SWITCH
50
562
221
595
RecalculateDrift
RecalculateDrift
0
1
-1000

MONITOR
1742
644
1856
689
NIL
RecalculateDrift
1
1
11

BUTTON
62
608
212
641
NIL
save-world-state
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SLIDER
213
900
382
933
ShockZone2Starts
ShockZone2Starts
-300
100
-50.0
5
1
NIL
HORIZONTAL

SLIDER
210
935
382
968
ShockZone2Ends
ShockZone2Ends
-300
100
-20.0
5
1
NIL
HORIZONTAL

SLIDER
14
900
190
933
ShockZone1Starts
ShockZone1Starts
-300
100
-260.0
5
1
NIL
HORIZONTAL

SLIDER
14
936
192
969
ShockZone1Ends
ShockZone1Ends
-300
100
-250.0
5
1
NIL
HORIZONTAL

SLIDER
390
900
569
933
ReliefZone1Starts
ReliefZone1Starts
-300
100
-280.0
5
1
NIL
HORIZONTAL

SLIDER
389
936
568
969
ReliefZone1Ends
ReliefZone1Ends
-300
100
-270.0
5
1
NIL
HORIZONTAL

SLIDER
575
901
755
934
ReliefZone2Starts
ReliefZone2Starts
-300
100
-200.0
5
1
NIL
HORIZONTAL

SLIDER
575
937
754
970
ReliefZone2Ends
ReliefZone2Ends
-300
100
-190.0
5
1
NIL
HORIZONTAL

SLIDER
766
903
976
936
ShockZone1Increase
ShockZone1Increase
0
100
35.0
1
1
NIL
HORIZONTAL

SLIDER
766
941
976
974
ShockZone2Increase
ShockZone2Increase
0
100
20.0
1
1
NIL
HORIZONTAL

SLIDER
981
903
1193
936
ReliefZone1Decrease
ReliefZone1Decrease
0
100
21.0
1
1
NIL
HORIZONTAL

SLIDER
982
941
1194
974
ReliefZone2Decrease
ReliefZone2Decrease
0
100
22.0
1
1
NIL
HORIZONTAL

MONITOR
1124
655
1250
700
GoodExit6Months
GoodExit6Months
0
1
11

MONITOR
1126
705
1260
750
GoodExit18Months
GoodExit18Months
0
1
11

MONITOR
1127
756
1261
801
GoodExit24Months
GoodExit24Months
0
1
11

MONITOR
1125
808
1259
853
GoodExit36Months
GoodExit36Months
0
1
11

SLIDER
1218
904
1390
937
DriftModifier
DriftModifier
1
10
1.01
0.01
1
NIL
HORIZONTAL

MONITOR
1266
655
1344
700
Bottom6Mo
BadExit6Months
0
1
11

MONITOR
1267
705
1351
750
Bottom18Mo
BadExit18Months
0
1
11

MONITOR
1268
759
1353
804
Bottom24Mo
BadExit24Months
0
1
11

MONITOR
1269
808
1356
853
Bottom36Mo
BadExit36Months
0
1
11

MONITOR
1265
854
1363
899
Bottom36+Mo
BadExit36PlusMonths
0
1
11

MONITOR
1124
853
1263
898
NeutralExit36PlusMonths
NeutralExit36PlusMonths
0
1
11

MONITOR
1430
655
1511
700
% Good exit
100 * ( GoodExit6Months + GoodExit18Months + GoodExit24Months + GoodExit36Months ) / \n\n( TotalClients - count clients )
1
1
11

MONITOR
1427
704
1508
749
% Bad exit
100 * count clients with [RockBottom = 1] \n/ \n(TotalClients - count clients)
1
1
11

SLIDER
1697
694
1869
727
Solicitors
Solicitors
0
100
50.0
1
1
NIL
HORIZONTAL

MONITOR
1428
754
1534
799
CommonLaw#
CommonLawCapture
1
1
11

MONITOR
1428
805
1537
850
CommonLaw%
100 * CommonLawCapture /\n(TotalClients - count clients)
1
1
11

BUTTON
79
464
196
497
reset-shock
reset-shock
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

MONITOR
1527
658
1628
703
NIL
costs
0
1
11

PLOT
1654
779
1854
929
Cost per client
NIL
NIL
0.0
10.0
0.0
10.0
true
false
"" ""
PENS
"default" 1.0 0 -16777216 true "" "plot costs / count clients"

@#$#@#$#@
## WHAT IS IT?

This model simulates a distance running-related injury (RRI) system. The conceptual basis for this model is as follows: there is a fixed sample of runners, each of whom has a number of defining characteristics (e.g. footwear, biomechanics, history of previous injury). There is also a fixed sample of healthcare professionals (e.g. physiotherapists, sports medicine doctors), who have a variable level of therapeutic efficacy. The model starts under the assumption that the runners have never run before, which means that their load capacity starts at 1.0, their external load expressed as weekly distance in kilometers is 0.0, and no single runner has had a history of previous injury.

The underlying premise is that once a given runner's load capacity falls below 0.5, their risk of sustaining injury increases. However, load capacity is affected for better or worse (i.e. training adaptation or maladaptation) based on the applied external workload, as well as other model parameters. The vanilla model, which runs over a 40 years period, sees runners in the system flow through either the RRI or no-RRI cycle.

## HOW IT WORKS

The model uses "couples" to represent two people engaged in sexual relations.  Individuals wander around the world when they are not in couples.  Upon coming into contact with a suitable partner, there is a chance the two individuals will "couple" together.  When this happens, the two individuals no longer move about, they stand next to each other holding hands as a representation of two people in a sexual relationship.

The presence of the virus in the population is represented by the colors of individuals. Three colors are used: green individuals are uninfected, blue individuals are infected but their infection is unknown, and red individuals are infected and their infection is known.

## HOW TO USE IT

The SETUP button creates individuals with particular behavioral tendencies according to the values of the interface's five sliders (described below).  Once the simulation has been setup, you are now ready to run it, by pushing the GO button.  GO starts the simulation and runs it continuously until GO is pushed again.  During a simulation initiated by GO, adjustments in sliders can affect the behavioral tendencies of the population.

A monitor shows the percent of the population that is infected by HIV.  In this model each time-step is considered one week; the number of weeks that have passed is shown in the toolbar.

Here is a summary of the sliders in the model.  They are explained in more detail below.

- INITIAL-PEOPLE: How many people simulation begins with.
- AVERAGE-COUPLING-TENDENCY: General likelihood member of population has sex (0--10).
- AVERAGE-COMMITMENT: How many weeks sexual relationships typically lasts (0--200).
- AVERAGE-CONDOM-USE: General chance member of population uses a condom. (0--10).
- AVERAGE-TEST-FREQUENCY: Average frequency member of population will check their HIV status in a 1-year time period (0--2).

The total number of individuals in the simulation is controlled by the slider INITIAL-PEOPLE (initialized to vary between 50--500), which must be set before SETUP is pushed.

During the model's setup procedures, all individuals are given "tendencies" according to four additional sliders.  These tendencies are generally assigned in a normal distribution, so that, for instance, if a tendency slider is set at 8, the most common value for that tendency in the population will be 8.  Less frequently, individuals will have values 7 or 9 for that tendency, and even less frequently will individuals have values 6 or 10 (and so on).

The slider AVERAGE-COUPLING-TENDENCY (0--10) determines the tendency of the individuals to become involved in couples (as stated earlier, all couples are presumed to be sexually active). When the AVERAGE-COUPLING-TENDENCY slider is set at zero, coupling is unlikely, although (because tendencies are assigned in a normal distribution) it is still possible.  Note that when deciding to couple, both individuals involved must be "willing" to do so, so high coupling tendencies in two individuals are actually compounded (i.e. two individuals with a 50% chance of coupling actually only have a 25% of coupling in a given tick).

The slider AVERAGE-COMMITMENT (1--200) determines how long individuals are likely to stay in a couple (in weeks).  Again, the tendencies of both individuals in a relationship are considered; the relationship only lasts as long as is allowed by the tendency of the partner with a shorter commitment tendency.

The slider AVERAGE-CONDOM-USE (0--10) determines the tendency of individuals in the population to practice safe sex.  If an individual uses a condom, it is assumed in this model that no infection by HIV is possible.  Note that this tendency (like the others) is probabilistic at several levels.  For instance, when AVERAGE-CONDOM-USE is set to 9, most of the individuals have a CONDOM-USE value of 9, although some have CONDOM-USE values of 8 or 10, and fewer yet have CONDOM-USE values of 7 or 11 (11 would be off-scale and the same as 10 for all practical purposes).  Also, an individual with a CONDOM-USE value of 9 will still sometimes not choose to use a condom (10% of the time, roughly). Simulation of condom-use is further complicated by the fact that if one partner "wants" to use a condom while the other partner does not, the couple does not use condoms.  This characteristic of the model is representative of the dynamics of some sexual relations, but not others.  The decision was somewhat arbitrary, and the user is invited to play with this characteristic and others in the "Extending the Model" section of this tab.

The slider AVERAGE-TEST-FREQUENCY (0--2) is the final slider of the interface.  It determines the average frequency of an individual to get tested for HIV in a one-year time span.  Set at 1.0, the average person will get tested for HIV about once a year.  Set at 0.2, the average individual will only get tested about every five years.  This tendency has significant impact because the model assumes that individuals who know that they are infected always practice safe sex, even if their tendency as healthy individuals was different.  Again, this characteristic of the model is only represented of the behaviors of some individuals.  The model was designed in this way to highlight the public health effects associated with frequent testing *and* appropriate responses to knowledge of one's HIV status.  To explore the impact of alternative behaviors on public health, the model code can be changed relatively painlessly.  These changes are described in the section, "Extending the Model."

The model's plot shows the total number of uninfected individuals (green), infected individuals whose positive status is not known (blue), and infected individuals whose positive status is known (red).

## THINGS TO NOTICE

Set the INITIAL-PEOPLE slider to 300, AVERAGE-COUPLING-TENDENCY to 10, AVERAGE-COMMITMENT to 100, and the other two sliders to 0. Push SETUP and then push GO. Notice that many individuals rapidly pair up into stationary "couples", with the patches behind them turned a dark gray.  These couples represent sexual activity between the two individuals.  Individuals that continue to move about (and do not have a gray patch behind them) are not engaging in sexual relations.  With RELATIONSHIP-DURATION set to 100, an average individual will have monogamous sexual relations with a partner for about 100 weeks (approximately 2 years) before ending the sexual relationship and searching out a new partner.

Stop the simulation (by pressing the GO button once again), move the AVERAGE-COUPLING-TENDENCY slider to 0, push SETUP, and start the simulation again (with the GO button).  Notice that this time, couples are not forming.  With a low COUPLING-TENDENCY, individuals do not choose to have sex, which in this model is depicted by the graphical representation of couplehood.  Notice that with these settings, HIV does not typically spread. However, spreading could happen since the population's tendency is set according to a normal distribution and a few people will probably have COUPLING-TENDENCY values above 0 at this setting.

Again reset the simulation, this time with AVERAGE-COUPLING-TENDENCY set back to 10 and AVERAGE-COMMITMENT set to 1.  Notice that while individuals do not stand still next to each other for any noticeable length of time, coupling is nevertheless occurring.  This is indicated by the occasional flash of dark gray behind individuals that are briefly next to one another.  Notice that the spread of HIV is actually faster when RELATIONSHIP-DURATION is very short compared to when it is very long.

Now run a simulation with AVERAGE-COMMITMENT equal to 1, AVERAGE-COUPLING-TENDENCY set to 1, AVERAGE-CONDOM-USE set to 10, and AVERAGE-TEST-FREQUENCY set to 1.0. With negligible couple formation and high condom use anyway, there should be no spread of HIV.  Notice how pale blue "infection unknown" individuals turn red much quicker in this simulation.  When the individuals turn red, they know their HIV status.  Some individuals turn red because they have been infected for long enough that they develop symptoms, which alerts them to the need for an HIV test.  Others become red because they choose to be tested.  With AVERAGE-TEST-FREQUENCY set to 1.0, healthy individuals are also being tested, but their color does not change since the tests come back negative.

When all the individuals in the simulation are either green or red, change the sliders without stopping the simulation.  Set AVERAGE-COUPLING-TENDENCY to 10, AVERAGE-COMMITMENT to 100, AVERAGE-CONDOM-USE to 0, and AVERAGE-TEST-FREQUENCY to 0.  Notice that despite the immediate formation of couples and the fact that condom-use tendency is presumably very low, some between healthy (green) individuals and infected (red) individuals, no spread of HIV occurs.  This is because while the model expects HIV positive individuals to continue to engage in sexual relations, it presumes that those individuals will always use condoms and that the condoms will always work.  The rationale for this design is discussed briefly in the "What is it?" section of this tab.

Finally, set INITIAL-PEOPLE to 500 to notice that couples can form on top of each other.  Watch the simulation until you see individuals by themselves, but standing still and with a gray patch behind them indicating coupling.  Underneath one of its neighbors, is the individuals partner.  This apparent bug in the program is necessary because individuals need to be able to couple fairly freely.  If space constraints prohibited coupling, unwanted emergent behavior would occur with high population densities.

Couples are formed by a partnership of "righty" and "lefty" shapes which is not immediately noticeable.  These shapes are not intended to represent genders in any fashion, but merely to provide a useful way to depict couple graphics. In order for the hands of a righty and lefty to match up, both must be off-centered in their patch.  Without this feature, two couples next to each other would appear to be a line of four individuals (instead of two groups of two).  It is intended that the differences between righty and lefty shapes not be especially apparent in order to prevent unintended associations with gender.  Any righty or lefty shape can be thought of as male or female or neither.

## THINGS TO TRY

Run a number of experiments with the GO button to find out the effects of different variables on the spread of HIV.  Try using good controls in your experiment.  Good controls are when only one variable is changed between trials.  For instance, to find out what effect the average duration of a relationship has, run four experiments with the AVERAGE-COMMITMENT slider set at 1 the first time, 2 the second time, 10 the third time, and 50 the last.  How much does the prevalence of HIV increase in each case?  Does this match your expectations?

Are the effects of some slider variables mediated by the effects of others?  For instance, if lower AVERAGE-COMMITMENT values seem to increase the spread of HIV when all other sliders are set at 0, does the same thing occur if all other sliders are set at 10?  You can run many experiments to test different hypotheses regarding the emergent public health effects associated with individual sexual behaviors.

## EXTENDING THE MODEL

Like all computer simulations of human behaviors, this model has necessarily simplified its subject area substantially.  The model therefore provides numerous opportunities for extension:

The model depicts sexual activity as two people standing next to each other.  This suggests that all couples have sex and that abstinence is only practiced in singlehood.  The model could be changed to reflect a more realistic view of what couples are.  Individuals could be in couples without having sex.  To show sex, then, a new graphical representation would have to be employed.  Perhaps sex could be symbolized by having the patches beneath the couple flash briefly to a different color.

The model does not distinguish between genders.  This is an obvious over-simplification chosen because making an exclusively heterosexual model was untenable while making one that included a variety of sexual preferences might have distracted from the public health issues which it was designed to explore.  However, extending the model by adding genders would make the model more realistic.

The model assumes that individuals who are aware that they are infected always practice safe sex.  This portrayal of human behavior is clearly not entirely realistic, but it does create interesting emergent behavior that has genuine relevance to certain public health debate.  However, an interesting extension of the model would be to change individuals' reactions to knowledge of HIV status.

The model assumes that condom use is always 100% effective.  In fact, responsible condom use is actually slightly less than ideal protection from the transmission of HIV.  Add a line of code to the INFECT procedure to check for a slight random chance that a particular episode of condom-use is not effective.  Another change that can be made in the INFECT procedure regards a couple's choice of condom-use.  In this model, when the two partners of a couple "disagree" about whether or not to use a condom, the partner that doesn't wish to use a condom makes the choice.  The opposite possibility is also valid.

Finally, certain significant changes can easily be made in the model by simply changing the value of certain global variables in the procedure SETUP-GLOBALS.  Two variables set in this procedure that are especially worthy of investigation are INFECTION-CHANCE and SYMPTOMS-SHOW.  The former controls what chance an infection has of spreading from an infected to an uninfected partner if no protection is used.  The variable is currently set to 50, meaning that in a week of sexual relations, the chance of infection occurring is 50%.  It is not clear at this time how realistic that figure is. SYMPTOMS-SHOW is the variable that controls how long, on average, a person will have the HIV virus before symptoms begin to appear, alerting that person to the presence of some health problem.  It is currently set to 200 (weeks) in this model.

## NETLOGO FEATURES

Notice that the four procedures that assign the different tendencies generate many small random numbers and add them together.  This produces a normal distribution of tendency values.  A random number between 0 and 100 is as likely to be 1 as it is to be 99. However, the sum of 20 numbers between 0 and 5 is much more likely to be 50 than it is to be 99.

Notice that the global variables SLIDER-CHECK-1, SLIDER-CHECK-2, and so on are assigned with the values of the various sliders so that the model can check the sliders against the variables while the model is running.  Every time-step, a slider's value is checked against a global variable that holds the value of what the slider's value was the time-step before.  If the slider's current value is different than the global variable, NetLogo knows to call procedures that adjust the population's tendencies.  Otherwise, those adjustment procedures are not called.

## CREDITS AND REFERENCES

Special thanks to Steve Longenecker for model development.

## HOW TO CITE

If you mention this model or the NetLogo software in a publication, we ask that you include the citations below.

For the model itself:

* Wilensky, U. (1997).  NetLogo AIDS model.  http://ccl.northwestern.edu/netlogo/models/AIDS.  Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.

Please cite the NetLogo software as:

* Wilensky, U. (1999). NetLogo. http://ccl.northwestern.edu/netlogo/. Center for Connected Learning and Computer-Based Modeling, Northwestern University, Evanston, IL.

## COPYRIGHT AND LICENSE

Copyright 1997 Uri Wilensky.

![CC BY-NC-SA 3.0](http://ccl.northwestern.edu/images/creativecommons/byncsa.png)

This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License.  To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/3.0/ or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA.

Commercial licenses are also available. To inquire about commercial licenses, please contact Uri Wilensky at uri@northwestern.edu.

This model was created as part of the project: CONNECTED MATHEMATICS: MAKING SENSE OF COMPLEX PHENOMENA THROUGH BUILDING OBJECT-BASED PARALLEL MODELS (OBPML).  The project gratefully acknowledges the support of the National Science Foundation (Applications of Advanced Technologies Program) -- grant numbers RED #9552950 and REC #9632612.

This model was converted to NetLogo as part of the projects: PARTICIPATORY SIMULATIONS: NETWORK-BASED DESIGN FOR SYSTEMS LEARNING IN CLASSROOMS and/or INTEGRATED SIMULATION AND MODELING ENVIRONMENT. The project gratefully acknowledges the support of the National Science Foundation (REPP & ROLE programs) -- grant numbers REC #9814682 and REC-0126227. Converted from StarLogoT to NetLogo, 2001.

<!-- 1997 2001 -->
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

person lefty
false
0
Circle -7500403 true true 170 5 80
Polygon -7500403 true true 165 90 180 195 150 285 165 300 195 300 210 225 225 300 255 300 270 285 240 195 255 90
Rectangle -7500403 true true 187 79 232 94
Polygon -7500403 true true 255 90 300 150 285 180 225 105
Polygon -7500403 true true 165 90 120 150 135 180 195 105

person righty
false
0
Circle -7500403 true true 50 5 80
Polygon -7500403 true true 45 90 60 195 30 285 45 300 75 300 90 225 105 300 135 300 150 285 120 195 135 90
Rectangle -7500403 true true 67 79 112 94
Polygon -7500403 true true 135 90 180 150 165 180 105 105
Polygon -7500403 true true 45 90 0 150 15 180 75 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.0.3
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
<experiments>
  <experiment name="experiment" repetitions="10" runMetricsEveryStep="true">
    <setup>setup</setup>
    <go>go</go>
    <timeLimit steps="1000"/>
    <metric>count turtles</metric>
    <metric>mean [ EWratio ] of  Runners</metric>
    <metric>Mean [ ACRatio ] of runners</metric>
    <metric>count runners with [ newinjury? = true ] / count runners</metric>
    <metric>mean [ RecentWorkLoad7 ] of runners</metric>
    <metric>max [ RecentWorkload7 ] of runners</metric>
    <metric>min [ RecentWorkload7 ] of runners</metric>
    <enumeratedValueSet variable="EW_ACRatio">
      <value value="true"/>
      <value value="false"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Promotion">
      <value value="1"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Original">
      <value value="false"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="DailyRampupRate">
      <value value="1.05"/>
      <value value="1.1"/>
      <value value="1.2"/>
      <value value="1.25"/>
      <value value="1.3"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Number_of_Events">
      <value value="0"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Enough">
      <value value="20"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="InitialRunners">
      <value value="1000"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="RandomVariation">
      <value value="0"/>
      <value value="0.01"/>
      <value value="0.025"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="InitialHCProfs">
      <value value="0"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Inspiration">
      <value value="1.01"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Ceiling_On">
      <value value="false"/>
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="InjuryRecovery">
      <value value="15"/>
    </enumeratedValueSet>
  </experiment>
</experiments>
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
