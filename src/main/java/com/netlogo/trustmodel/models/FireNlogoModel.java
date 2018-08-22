package com.netlogo.trustmodel.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FireNlogoModel {
    private final String density;
    private final String rand_seed;
    private final String reps;
    public String burnt_trees;
}
