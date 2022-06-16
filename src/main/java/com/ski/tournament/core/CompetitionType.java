package com.ski.tournament.core;

import java.util.HashMap;
import java.util.Map;

public enum CompetitionType {

    SINGLE_COMPETITIONS_SPONSOR_CUP("Pojedyncze zawody o puchar sponsora","SingleCompetitionsSponsorCup"),
    SINGLE_COMPETITIONS_UNIT_MASTERY("Pojedyncze zawody o mistrzostwo jednostki","SingleCompetitionsUnitMastery"),
    SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE("Pojedyncze zawody w danej konkurencji","SingleCompetitionsOneCompetitionType"),
    SINGLE_COMPETITIONS_TEAM_CLASSIFICATION("Pojedynczy zawody klasyfikacji drużynowej","SingleCompetitionsTeamClassification");

    private static final Map<String, CompetitionType> BY_LABEL1= new HashMap<>();
    private static final Map<String, CompetitionType> BY_LABEL2= new HashMap<>();


    static {
        for (CompetitionType e : values()) {
            BY_LABEL1.put(e.label1, e);
            BY_LABEL2.put(e.label2, e);
        }
    }

    public final String label1;

    public final String label2;

    public static CompetitionType valueOfLabel1(String label1) {
        return BY_LABEL1.get(label1);
    }

    public static CompetitionType valueOfLabel12(String label2) {
        return BY_LABEL2.get(label2);
    }

    CompetitionType(String label1, String label2) {
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
