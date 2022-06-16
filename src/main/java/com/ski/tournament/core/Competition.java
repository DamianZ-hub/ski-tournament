package com.ski.tournament.core;

import java.util.HashMap;
import java.util.Map;

public enum Competition {


//    SLALOM("Slalom"),
//    GIGANT("Gigant");


    SLALOM("Slalom","SL"),
    GIGANT("Gigant","GS");


    private static final Map<String, Competition> BY_LABEL1= new HashMap<>();
    private static final Map<String, Competition> BY_LABEL2= new HashMap<>();


    static {
        for (Competition e : values()) {
            BY_LABEL1.put(e.label1, e);
            BY_LABEL2.put(e.label2, e);
        }
    }

    public final String label1;

    public final String label2;

    public static Competition valueOfLabel1(String label1) {
        return BY_LABEL1.get(label1);
    }

    public static Competition valueOfLabel12(String label2) {
        return BY_LABEL2.get(label2);
    }

    Competition(String label1, String label2) {
        this.label1=label1;
        this.label2=label2;
    }

    public String getLabel1() {
        return label1;
    }

    public String getLabel2() {
        return label2;
    }
}
