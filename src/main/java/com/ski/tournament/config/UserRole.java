package com.ski.tournament.config;

import java.util.HashMap;
import java.util.Map;

public enum UserRole {

    ROLE_User("User"),
    ROLE_Staff("Staff"),
    ROLE_Admin("Admin");

    private static final Map<String, UserRole> BY_LABEL1= new HashMap<>();


    static {
        for (UserRole e : values()) {
            BY_LABEL1.put(e.label1, e);
        }
    }

    public final String label1;


    public static UserRole valueOfLabel1(String label1) {
        return BY_LABEL1.get(label1);
    }


    UserRole(String label1) {
        this.label1=label1;
    }

    public String getLabel1() {
        return label1;
    }
}
