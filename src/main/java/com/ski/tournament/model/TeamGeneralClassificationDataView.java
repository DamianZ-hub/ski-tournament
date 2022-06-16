package com.ski.tournament.model;



public class TeamGeneralClassificationDataView {

    private Integer takenPlace;

    private Unit unit;

    private Integer sumarizedScore;

    public TeamGeneralClassificationDataView() {
    }

    public TeamGeneralClassificationDataView(Integer takenPlace, Unit unit, Integer sumarizedScore) {
        this.takenPlace = takenPlace;
        this.unit = unit;
        this.sumarizedScore = sumarizedScore;
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

    public Integer getSumarizedScore() {
        return sumarizedScore;
    }

    public void setSumarizedScore(Integer sumarizedScore) {
        this.sumarizedScore = sumarizedScore;
    }
}
