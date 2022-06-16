package com.ski.tournament.core;

import java.util.HashMap;
import java.util.Map;

public enum SignStatus {

    PENDING("OCZEKUJĄCE"),
    ACCEPTED("ZAAKCEPTOWANE"),
    REJECTED("ODRZUCONE");


    private static final Map<String, SignStatus> BY_LABEL1= new HashMap<>();


    static {
        for (SignStatus e : values()) {
            BY_LABEL1.put(e.label1, e);
        }
    }

    public final String label1;


    public static SignStatus valueOfLabel1(String label1) {
        return BY_LABEL1.get(label1);
    }


    SignStatus(String label1) {
        this.label1=label1;
    }

    public String getLabel1() {
        return label1;
    }


}
