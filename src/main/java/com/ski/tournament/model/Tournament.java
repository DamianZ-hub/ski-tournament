package com.ski.tournament.model;


import com.ski.tournament.core.Competition;
import com.ski.tournament.core.CompetitionType;
import com.ski.tournament.core.TournamentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Tournament extends AbstractEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;
    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Competition competition;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CompetitionType competitionType;

    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Transient
    private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm";

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Competition getCompetition() {
        return competition;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }

    public Tournament() {
    }

    public Tournament(@NotNull LocalDateTime dateTime, @NotNull CompetitionType competitionType, @NotNull Place place, @NotNull Unit unit, @NotNull TournamentStatus status, @NotNull Competition competition) {
        this.status = status;
        this.dateTime = dateTime;
        this.competition = competition;
        this.unit = unit;
        this.place = place;
        this.competitionType = competitionType;
    }

    public Tournament(@NotNull LocalDateTime dateTime, @NotNull CompetitionType competitionType, @NotNull Place place, @NotNull Unit unit, @NotNull TournamentStatus status, @NotNull Competition competition, String description) {
        this.status = status;
        this.dateTime = dateTime;
        this.competition = competition;
        this.description = description;
        this.unit = unit;
        this.place = place;
        this.competitionType = competitionType;

    }

    public String getTournamentData() {
        return getDateTime().format(formatter) + " - " +
                getCompetition().label1 +
                (getDescription() == null || getDescription().isEmpty() ? "" : " - " +
                        getDescription());
    }
}
