package com.ski.tournament.views.startlist;

import com.ski.tournament.core.TwinColGridModified;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.service.PersonTournamentDataService;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StartListGrid extends TwinColGridModified<PersonTournamentData> implements BeforeEnterObserver {

    private Integer tournamentId;
    private AtomicInteger localMaxNr;

    private PersonTournamentDataService personTournamentDataService;

    public StartListGrid(PersonTournamentDataService personTournamentDataService, Integer tournamentId) {
        super();
        this.personTournamentDataService = personTournamentDataService;
        this.tournamentId = tournamentId;
    }

    public StartListGrid(List<PersonTournamentData> personTournamentDataList, String caption, PersonTournamentDataService personTournamentDataService, Integer tournamentId) {
        super(personTournamentDataList, caption);
        this.personTournamentDataService = personTournamentDataService;
        this.tournamentId = tournamentId;

    }



    @Override
    protected void updateSelection(Set<PersonTournamentData> addedItems, Set<PersonTournamentData> removedItems) {
        setLocalMaxNr(getMaxNr());
        super.updateSelection(assignNumbers(addedItems), deassignNumbers(removedItems));
    }

    protected void updateSelectionAutomatically(Set<PersonTournamentData> addedItems, Set<PersonTournamentData> removedItems, Integer maxNr) {
        setLocalMaxNr(maxNr);
        super.updateSelection(assignNumbers(addedItems), deassignNumbers(removedItems));
    }

    private Set<PersonTournamentData> assignNumbers(Set<PersonTournamentData> items){

        items.forEach(personTournamentData -> {
            if(personTournamentData.getNr()==null) personTournamentData.setNr(localMaxNr.addAndGet(1));
        });
        return items;
    }

    private Set<PersonTournamentData> deassignNumbers(Set<PersonTournamentData> items){
        items.forEach(personTournamentData -> {
            personTournamentData.setNr(null);
            localMaxNr.decrementAndGet();
        });
        return items;
    }


//    public AtomicInteger getLocalMaxNr() {
//        return localMaxNr;
//    }

    public void setLocalMaxNr(Integer localMaxNr) {
        this.localMaxNr = new AtomicInteger(localMaxNr);
    }


    private Integer getMaxNr(){
        List<Integer> nrList = getRightGrid().getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).stream().map(PersonTournamentData::getNr).collect(Collectors.toList());
        return nrList.isEmpty() ? 0 : Collections.max(nrList);
    }

    protected void resetGrid() {
        List<PersonTournamentData> filteredItems = (List) getRightGrid().getDataProvider().withConfigurableFilter().fetch(new Query()).collect(Collectors.toList());
        this.updateSelection(new HashSet(), new HashSet(filteredItems));
        refreshGrid();
    }
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
//        localMaxNr = new AtomicInteger(personTournamentDataService.getMaxNrForTournament(tournamentId, SignStatus.ACCEPTED));
        refreshGrid();
    }
}
