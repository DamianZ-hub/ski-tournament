package com.ski.tournament.core;

import java.util.HashMap;
import java.util.Map;

public enum ClassificationType {

    GENERAL_CLASSIFICATION_RECTOR_CUP("Klasyfikacja generalna o Puchar Rektora","GeneralClassificationRectorCup"),
    GENERAL_CLASSIFICATION_KU_AZS_CSIR_CUP("Klasyfikacja generalna o Puchar KU AZS i Puchar CSiR","GeneralClassificationKUAZSCSiRCup"),
    GENERAL_TEAM_CLASSIFICATION("Klasyfikacja generalna drużynowa","GeneralTeamClassification");


    private static final Map<String, ClassificationType> BY_LABEL1= new HashMap<>();
    private static final Map<String, ClassificationType> BY_LABEL2= new HashMap<>();


    static {
        for (ClassificationType e : values()) {
            BY_LABEL1.put(e.label1, e);
            BY_LABEL2.put(e.label2, e);
        }
    }

    public final String label1;

    public final String label2;

    public static ClassificationType valueOfLabel1(String label1) {
        return BY_LABEL1.get(label1);
    }

    public static ClassificationType valueOfLabel12(String label2) {
        return BY_LABEL2.get(label2);
    }

    ClassificationType(String label1, String label2) {
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
