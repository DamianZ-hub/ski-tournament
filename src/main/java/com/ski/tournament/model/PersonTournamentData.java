package com.ski.tournament.model;

import com.ski.tournament.core.SignStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class PersonTournamentData extends AbstractEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SignStatus signStatus;


    private Integer nr;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public SignStatus getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(SignStatus status) {
        this.signStatus = status;
    }

    public PersonTournamentData(@NotNull Person person, @NotNull Tournament tournament, @NotNull SignStatus signStatus) {
        this.person = person;
        this.tournament = tournament;
        this.signStatus = signStatus;
    }

    public Integer getNr() {
        return nr;
    }

    public void setNr(Integer nr) {
        this.nr = nr;
    }

    public PersonTournamentData() {
    }

    public String getPersonFirstName(){
        return person.getFirstName();
    }

    public String getPersonLastName(){
        return person.getLastName();
    }
    public String getPersonGender(){
        return person.getGender();
    }

    public String getNrAsString() {
        return String.valueOf(nr);
    }

    public String getNrFirstNameLastNameAndUnit(){
        return getNrAsString() + ". " + getPersonFirstName() + " " + getPersonLastName() + " " + getPerson().getUnit().getShortName();
    }
}
