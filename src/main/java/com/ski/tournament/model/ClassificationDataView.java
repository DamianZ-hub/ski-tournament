package com.ski.tournament.model;

import java.util.HashMap;


public class ClassificationDataView {

    private String firstName;

    private String lastName;

    private HashMap<Integer,Integer> scoreMap = new HashMap<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public HashMap<Integer, Integer> getScoreMap() {
        return scoreMap;
    }

    public void setScoreMap(HashMap<Integer, Integer> scoreMap) {
        this.scoreMap = scoreMap;
    }
}
