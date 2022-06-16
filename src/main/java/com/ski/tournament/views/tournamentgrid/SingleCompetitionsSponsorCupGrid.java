package com.ski.tournament.views.tournamentgrid;

import com.ski.tournament.model.SingleCompetitionsSponsorCupData;
import com.ski.tournament.service.SingleCompetitionsSponsorCupDataService;
import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import java.util.List;

public class SingleCompetitionsSponsorCupGrid extends VerticalLayout implements AfterNavigationObserver {


    private EnhancedGrid<SingleCompetitionsSponsorCupData> grid = new EnhancedGrid<>();

    private SingleCompetitionsSponsorCupData singleCompetitionsSponsorCupData;

    private SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService;

    private Integer tournamentID;

    public SingleCompetitionsSponsorCupGrid(SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService, Integer tornamentID) {
        this.singleCompetitionsSponsorCupDataService = singleCompetitionsSponsorCupDataService;
        this.tournamentID = tornamentID;

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<SingleCompetitionsSponsorCupData> takenPlaceColumn = grid.addColumn(SingleCompetitionsSponsorCupData::getTakenPlace).setHeader("Zajęte miejsce");
        grid.addColumn(singleCompetitionsSponsorCupData -> {
            return singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getFirstName();
        }).setHeader("Imie").setAutoWidth(true);
        grid.addColumn(singleCompetitionsSponsorCupData -> {
            return singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getLastName();
        }).setHeader("Nazwisko").setAutoWidth(true);
        grid.addColumn(singleCompetitionsSponsorCupData -> {
            return singleCompetitionsSponsorCupData.getPersonTournamentData().getPerson().getUnit().getFullName();
        }).setHeader("Jednostka").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsSponsorCupData::getFirstRideTime).setHeader("Czas I przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsSponsorCupData::getCorrectedFirstRideTime).setHeader("Skorygowany czas I przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsSponsorCupData::getSecondRideTime).setHeader("Czas II przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsSponsorCupData::getCorrectedSecondRideTime).setHeader("Skorygowany czas II przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsSponsorCupData::getSumariseCorrectedRideTime).setHeader("Sumaryczny czas przejazdów").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsSponsorCupData::getScore).setHeader("Liczba punktów").setAutoWidth(true);
        grid.addColumn(singleCompetitionsSponsorCupData -> {
            return singleCompetitionsSponsorCupData.getRideStatus()!=null ? singleCompetitionsSponsorCupData.getRideStatus().toString() : "";
        }).setHeader("Status przejazdu").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        List<GridSortOrder<SingleCompetitionsSponsorCupData>> sortByTakePlace = new GridSortOrderBuilder<SingleCompetitionsSponsorCupData>().thenDesc(takenPlaceColumn).build();
        grid.sort(sortByTakePlace);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }


    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        List<SingleCompetitionsSponsorCupData> singleCompetitionsSponsorCupData = singleCompetitionsSponsorCupDataService.getAllSingleCompetitionsSponsorCupDataByTournamentId(tournamentID);
        if(!singleCompetitionsSponsorCupData.isEmpty()) grid.setItems(singleCompetitionsSponsorCupData);

    }


}
