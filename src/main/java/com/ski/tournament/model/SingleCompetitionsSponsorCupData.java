package com.ski.tournament.model;

import com.ski.tournament.core.RideStatus;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class SingleCompetitionsSponsorCupData extends AbstractEntity {

    @NotNull
    @OneToOne
    @JoinColumn(name = "person_tournament_id")
    private PersonTournamentData personTournamentData;
    private Double firstRideTime;
    private Double correctedFirstRideTime;
    private Double secondRideTime;
    private Double correctedSecondRideTime;
    private Double sumariseCorrectedRideTime;
    private Integer score;
    private Integer takenPlace;

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

    public Double getCorrectedFirstRideTime() {
        return correctedFirstRideTime;
    }

    public void setCorrectedFirstRideTime(Double correctedFirstRideTime) {
        this.correctedFirstRideTime = correctedFirstRideTime;
    }

    public Double getSecondRideTime() {
        return secondRideTime;
    }

    public void setSecondRideTime(Double secondRideTime) {
        this.secondRideTime = secondRideTime;
    }

    public Double getCorrectedSecondRideTime() {
        return correctedSecondRideTime;
    }

    public void setCorrectedSecondRideTime(Double correctedSecondRideTime) {
        this.correctedSecondRideTime = correctedSecondRideTime;
    }

    public Double getSumariseCorrectedRideTime() {
        return sumariseCorrectedRideTime;
    }

    public void setSumariseCorrectedRideTime(Double sumariseCorrectedRideTime) {
        this.sumariseCorrectedRideTime = sumariseCorrectedRideTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public SingleCompetitionsSponsorCupData(@NotNull PersonTournamentData personTournamentData,
                                            int takenPlace, double firstRideTime, double correctedFirstRideTime,
                                            double secondRideTime, double correctedSecondRideTime,
                                            double sumariseCorrectedRideTime, int score) {

        this.personTournamentData = personTournamentData;
        this.takenPlace = takenPlace;
        this.firstRideTime = firstRideTime;
        this.correctedFirstRideTime = correctedFirstRideTime;
        this.secondRideTime = secondRideTime;
        this.correctedSecondRideTime = correctedSecondRideTime;
        this.sumariseCorrectedRideTime = sumariseCorrectedRideTime;
        this.score = score;
    }

    public SingleCompetitionsSponsorCupData() {
    }
}
