package com.ski.tournament.model;

import java.util.List;

public class SingleCompetitionsTeamCompetitionData {


    private Integer takenPlace;
    private Unit unit;

    private List<Integer> bestManScore;
    private List<Integer> bestWomanScore;


    private Integer sumarizedScore;

    public SingleCompetitionsTeamCompetitionData(Integer takenPlace, Unit unit, List<Integer> bestManScore, List<Integer> bestWomanScore, Integer sumarizedScore) {
        this.takenPlace = takenPlace;
        this.unit = unit;
        this.bestManScore = bestManScore;
        this.bestWomanScore = bestWomanScore;
        this.sumarizedScore = sumarizedScore;
    }

    public SingleCompetitionsTeamCompetitionData() {
    }

    public Integer getTakenPlace() {
        return takenPlace;
    }

    public void setTakenPlace(Integer takenPlace) {
        this.takenPlace = takenPlace;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public List<Integer> getBestManScore() {
        return bestManScore;
    }

    public void setBestManScore(List<Integer> bestManScore) {
        this.bestManScore = bestManScore;
    }

    public List<Integer> getBestWomanScore() {
        return bestWomanScore;
    }

    public void setBestWomanScore(List<Integer> bestWomanScore) {
        this.bestWomanScore = bestWomanScore;
    }

    public Integer getSumarizedScore() {
        return sumarizedScore;
    }

    public void setSumarizedScore(Integer sumarizedScore) {
        this.sumarizedScore = sumarizedScore;
    }

    public String getUnitShortName() {
        return unit.getShortName();
    }
}
