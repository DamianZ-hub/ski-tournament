package com.ski.tournament.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Unit extends AbstractEntity {

    @NotNull
    private String fullName;

    @NotNull
    private String shortName;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "unit_id")
//    private List<Tournament> tournaments = new ArrayList<>();

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }





    public Unit(@NotNull String fullName,@NotNull String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public Unit() {
    }

    public static Unit findUnitByFullName(Collection<Unit> unitsList, String fullName) {
        return unitsList.stream().filter(unit -> fullName.equals(unit.getFullName())).findFirst().orElse(null);
    }

}
