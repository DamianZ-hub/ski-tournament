package com.ski.tournament.views.tournamentgrid;

import com.ski.tournament.core.TextFilterField;
import com.ski.tournament.model.SingleCompetitionsOneCompetitionTypeData;
import com.ski.tournament.service.SingleCompetitionsOneCompetitionTypeDataService;
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

public class SingleCompetitionsOneCompetitionTypeGrid extends VerticalLayout implements AfterNavigationObserver {


    private EnhancedGrid<SingleCompetitionsOneCompetitionTypeData> grid = new EnhancedGrid<>();

    private SingleCompetitionsOneCompetitionTypeData singleCompetitionsOneCompetitionTypeData;

    private SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;

    private Integer tournamentID;

    public SingleCompetitionsOneCompetitionTypeGrid(@Autowired SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService, Integer tournamentID) {
        this.singleCompetitionsOneCompetitionTypeDataService = singleCompetitionsOneCompetitionTypeDataService;
        this.tournamentID = tournamentID;
        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<SingleCompetitionsOneCompetitionTypeData> takenPlaceColumn = grid.addColumn(SingleCompetitionsOneCompetitionTypeData::getTakenPlace).setHeader("Zajęte miejsce");
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> {
            return singleCompetitionsOneCompetitionTypeData.getPersonTournamentData().getPerson().getFirstName();
        }).setHeader("Imie").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> {
            return singleCompetitionsOneCompetitionTypeData.getPersonTournamentData().getPerson().getLastName();
        }).setHeader("Nazwisko").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> {
            return singleCompetitionsOneCompetitionTypeData.getPersonTournamentData().getPerson().getUnit().getFullName();
        }).setHeader("Jednostka").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsOneCompetitionTypeData::getFirstRideTime).setHeader("Czas I przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsOneCompetitionTypeData::getSecondRideTime).setHeader("Czas II przejazdu").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsOneCompetitionTypeData::getSumariseRideTime).setHeader("Sumaryczny czas przejazdów").setAutoWidth(true);
        grid.addColumn(SingleCompetitionsOneCompetitionTypeData::getScore).setHeader("Liczba punktów").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> {
            return singleCompetitionsOneCompetitionTypeData.getRideStatus()!=null ? singleCompetitionsOneCompetitionTypeData.getRideStatus().toString() : "";
        }).setHeader("Status przejazdu").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        List<GridSortOrder<SingleCompetitionsOneCompetitionTypeData>> sortByTakePlace = new GridSortOrderBuilder<SingleCompetitionsOneCompetitionTypeData>().thenDesc(takenPlaceColumn).build();
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
        List<SingleCompetitionsOneCompetitionTypeData> singleCompetitionsOneCompetitionTypeDataList = singleCompetitionsOneCompetitionTypeDataService.getAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(tournamentID);
        if (!singleCompetitionsOneCompetitionTypeDataList.isEmpty())
            grid.setItems(singleCompetitionsOneCompetitionTypeDataList);

    }


}

