package com.ski.tournament.core;

import java.util.HashMap;
import java.util.Map;

public enum RideStatus {

    DS("Did Start"),
    DNS("Did Not Start"),
    DNF("Did Not Finish"),
    DSQ("Disqualified");


    private static final Map<String, RideStatus> BY_LABEL1= new HashMap<>();


    static {
        for (RideStatus e : values()) {
            BY_LABEL1.put(e.label1, e);
        }
    }

    public final String label1;


    public static RideStatus valueOfLabel1(String label1) {
        return BY_LABEL1.get(label1);
    }


    RideStatus(String label1) {
        this.label1=label1;
    }

    public String getLabel1() {
        return label1;
    }


}
