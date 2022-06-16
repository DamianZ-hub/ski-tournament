package com.ski.tournament.views.tournamentgrid;

import com.ski.tournament.model.SingleCompetitionsTeamCompetitionData;
import com.ski.tournament.service.SingleCompetitionsTeamCompetitionsDataService;
import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import java.util.List;

public class SingleCompetitionsTeamCompetitionGrid extends VerticalLayout implements AfterNavigationObserver {


    private EnhancedGrid<SingleCompetitionsTeamCompetitionData> grid = new EnhancedGrid<>();

    private List<SingleCompetitionsTeamCompetitionData> singleCompetitionsTeamCompetitionDataList;

    private SingleCompetitionsTeamCompetitionsDataService singleCompetitionsTeamCompetitionsDataService;

    private Integer tournamentID;

    private String competitionType;

    public SingleCompetitionsTeamCompetitionGrid(SingleCompetitionsTeamCompetitionsDataService singleCompetitionsTeamCompetitionsDataService, Integer tournamentID, String competitionType) {
        this.singleCompetitionsTeamCompetitionsDataService = singleCompetitionsTeamCompetitionsDataService;
        this.tournamentID = tournamentID;
        this.competitionType = competitionType;

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<SingleCompetitionsTeamCompetitionData> takenPlaceColumn = grid.addColumn(SingleCompetitionsTeamCompetitionData::getTakenPlace).setHeader("Zajęte miejsce");
        grid.addColumn(classificationDataView -> {
            return classificationDataView.getUnit().getFullName();
        }).setHeader("Jednostka");

        grid.addColumn(classificationDataView -> {
            return classificationDataView.getBestManScore().get(0);
         }).setHeader("Pierwszy mężczyzna").setAutoWidth(true);
        grid.addColumn(classificationDataView -> {
            return classificationDataView.getBestManScore().get(1);
        }).setHeader("Drugi mężczyzna").setAutoWidth(true);
        grid.addColumn(classificationDataView -> {
            return classificationDataView.getBestManScore().get(2);
        }).setHeader("Trzeci mężczyzna").setAutoWidth(true);

        grid.addColumn(classificationDataView -> {
            return classificationDataView.getBestWomanScore().get(0);
        }).setHeader("Pierwsza kobieta").setAutoWidth(true);
        grid.addColumn(classificationDataView -> {
            return classificationDataView.getBestWomanScore().get(1);
        }).setHeader("Druga kobieta").setAutoWidth(true);
        grid.addColumn(classificationDataView -> {
            return classificationDataView.getBestWomanScore().get(2);
        }).setHeader("Trzecia kobieta").setAutoWidth(true);

        grid.addColumn(classificationDataView -> {
            return classificationDataView.getSumarizedScore();
        }).setHeader("Suma").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        List<GridSortOrder<SingleCompetitionsTeamCompetitionData>> sortByTakePlace = new GridSortOrderBuilder<SingleCompetitionsTeamCompetitionData>().thenAsc(takenPlaceColumn).build();
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
        singleCompetitionsTeamCompetitionDataList = singleCompetitionsTeamCompetitionsDataService.getTeamCompetitionDatas(tournamentID, competitionType);
        if(!singleCompetitionsTeamCompetitionDataList.isEmpty()) {
            new Notification("Brak rekordów dla tabeli drużynowej");
            grid.setItems(singleCompetitionsTeamCompetitionDataList);
        }

    }


}
