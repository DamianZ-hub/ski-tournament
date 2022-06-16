package com.ski.tournament.views.tournamentgrid;

import com.ski.tournament.model.SingleCompetitionsUnitMasteryData;
import com.ski.tournament.service.SingleCompetitionsUnitMasteryDataService;
import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SingleCompetitionsUnitMasteryGrid extends VerticalLayout implements AfterNavigationObserver {


    private EnhancedGrid<SingleCompetitionsUnitMasteryData> grid = new EnhancedGrid<>();

    private SingleCompetitionsUnitMasteryData singleCompetitionsUnitMasteryData;

    private SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;

    private Integer tournamentID;

    public SingleCompetitionsUnitMasteryGrid(@Autowired SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService, Integer tournamentID) {
        this.singleCompetitionsUnitMasteryDataService = singleCompetitionsUnitMasteryDataService;
        this.tournamentID = tournamentID;

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<SingleCompetitionsUnitMasteryData> takenPlaceColumn = grid.addColumn(SingleCompetitionsUnitMasteryData::getTakenPlace).setHeader("Zajęte miejsce");
        grid.addColumn(singleCompetitionsUnitMasteryData -> {
            return singleCompetitionsUnitMasteryData.getPersonTournamentData().getPerson().getFirstName();
        }).setHeader("Imie").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> {
            return singleCompetitionsUnitMasteryData.getPersonTournamentData().getPerson().getLastName();
        }).setHeader("Nazwisko").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsUnitMasteryData::getFirstRideTime).setHeader("Czas I przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsUnitMasteryData::getSecondRideTime).setHeader("Czas II przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsUnitMasteryData::getSumariseRideTime).setHeader("Sumaryczny czas przejazdów").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> {
            return singleCompetitionsUnitMasteryData.getRideStatus()!=null ? singleCompetitionsUnitMasteryData.getRideStatus().toString() : "";
        }).setHeader("Status przejazdu").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        List<GridSortOrder<SingleCompetitionsUnitMasteryData>> sortByTakePlace = new GridSortOrderBuilder<SingleCompetitionsUnitMasteryData>().thenDesc(takenPlaceColumn).build();
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
        List<SingleCompetitionsUnitMasteryData> singleCompetitionsUnitMasteryData = singleCompetitionsUnitMasteryDataService.getAllSingleCompetitionsUnitMasteryDataByTournamentId(tournamentID);
        if(!singleCompetitionsUnitMasteryData.isEmpty()) grid.setItems(singleCompetitionsUnitMasteryData);
    }

}
