package com.ski.tournament.core;

public enum TournamentStatus {
    INCOMING("NADCHODZĄCE"),
    ONGOING("TRWAJĄCE"),
    PAST("PRZESZŁE"),
    CANCELED("ANULOWANE");


    public final String label;

    TournamentStatus(String label) {
        this.label=label;
    }

    public String getLabel() {
        return label;
    }
}
