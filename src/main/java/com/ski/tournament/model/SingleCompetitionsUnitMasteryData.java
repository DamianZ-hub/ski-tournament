package com.ski.tournament.model;

import com.ski.tournament.core.RideStatus;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class SingleCompetitionsUnitMasteryData extends AbstractEntity {

    @NotNull
    @OneToOne
    @JoinColumn(name = "person_tournament_id")
    private PersonTournamentData personTournamentData;
    private Integer takenPlace;
    private Double firstRideTime;
    private Double secondRideTime;
    private Double sumariseRideTime;

    private RideStatus rideStatus;

    public RideStatus getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }


    public PersonTournamentData getPersonTournamentData() {
        return personTournamentData;
    }

    public void setPersonTournamentData(PersonTournamentData personTournamentData) {
        this.personTournamentData = personTournamentData;
    }

    public Integer getTakenPlace() {
        return takenPlace;
    }

    public void setTakenPlace(Integer takenPlace) {
        this.takenPlace = takenPlace;
    }

    public Double getFirstRideTime() {
        return firstRideTime;
    }

    public void setFirstRideTime(Double firstRideTime) {
        this.firstRideTime = firstRideTime;
    }

    public Double getSecondRideTime() {
        return secondRideTime;
    }

    public void setSecondRideTime(Double secondRideTime) {
        this.secondRideTime = secondRideTime;
    }

    public Double getSumariseRideTime() {
        return sumariseRideTime;
    }

    public void setSumariseRideTime(Double sumariseRideTime) {
        this.sumariseRideTime = sumariseRideTime;
    }

    public SingleCompetitionsUnitMasteryData(@NotNull PersonTournamentData personTournamentData, int takenPlace, double firstRideTime, double secondRideTime, double sumariseRideTime) {
        this.personTournamentData = personTournamentData;
        this.takenPlace = takenPlace;
        this.firstRideTime = firstRideTime;
        this.secondRideTime = secondRideTime;
        this.sumariseRideTime = sumariseRideTime;
    }

    public SingleCompetitionsUnitMasteryData() {
    }
}