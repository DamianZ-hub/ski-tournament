package com.ski.tournament.views.classificationGrid;

import com.ski.tournament.model.Classification;
import com.ski.tournament.model.TeamGeneralClassificationDataView;
import com.ski.tournament.service.ClassificationService;
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
import java.util.Set;

public class GeneralTeamClassificationGrid extends VerticalLayout implements AfterNavigationObserver {

    private EnhancedGrid<TeamGeneralClassificationDataView> grid = new EnhancedGrid<>();

    private Classification classification;
    private Integer classificationID;
    private List<Integer> tournamentsIds;
    private ClassificationService classificationService;

    public GeneralTeamClassificationGrid(ClassificationService classificationService,
                                         Integer classificationID) {
        this.classificationService = classificationService;
        this.classificationID = classificationID;
        classification = classificationService.get(classificationID).orElseThrow(RuntimeException::new);
        Set<Integer> tournamentsIdsSet = classification.getTablesIds();
        tournamentsIds = List.copyOf(tournamentsIdsSet);

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<TeamGeneralClassificationDataView> takenPlaceColumn = grid.addColumn(TeamGeneralClassificationDataView::getTakenPlace).setHeader("Zajęte miejsce");
        grid.addColumn(teamGeneralClassificationDataView -> {
            return teamGeneralClassificationDataView.getUnit().getShortName();
        }).setHeader("Jednostka");
        grid.addColumn(teamGeneralClassificationDataView -> {
            return teamGeneralClassificationDataView.getSumarizedScore();
        }).setHeader("Punkty").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        List<GridSortOrder<TeamGeneralClassificationDataView>> sortBySum = new GridSortOrderBuilder<TeamGeneralClassificationDataView>().thenAsc(takenPlaceColumn).build();
        grid.sort(sortBySum);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        add(grid);
    }


    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {


        List<TeamGeneralClassificationDataView> teamGeneralClassificationDataViewList = classificationService.getTeamGeneralClassificationDataViewList(tournamentsIds);
        if (!teamGeneralClassificationDataViewList.isEmpty()) grid.setItems(teamGeneralClassificationDataViewList);
        else new Notification("Brak rekordów generalnej klasyfikacji drużynowej");

    }


}
