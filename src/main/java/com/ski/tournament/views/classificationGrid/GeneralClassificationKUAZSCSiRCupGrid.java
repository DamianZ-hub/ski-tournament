package com.ski.tournament.views.classificationGrid;

import com.ski.tournament.model.Classification;
import com.ski.tournament.model.ClassificationDataView;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.model.Tournament;
import com.ski.tournament.service.ClassificationService;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.service.TournamentService;
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
import java.util.Set;

public class GeneralClassificationKUAZSCSiRCupGrid extends VerticalLayout implements AfterNavigationObserver {

    private EnhancedGrid<ClassificationDataView> grid = new EnhancedGrid<>();

    private Classification classification;
    private Integer classificationID;
    private List<Integer> tournamentsIds;
    private List<Tournament> tournamentList;
    private List<PersonTournamentData> personTournamentDataList;
    private ClassificationService classificationService;
    private PersonTournamentDataService personTournamentDataService;
    private TournamentService tournamentService;

    public GeneralClassificationKUAZSCSiRCupGrid(ClassificationService classificationService,
                                                 PersonTournamentDataService personTournamentDataService,
                                                 TournamentService tournamentService,
                                                 Integer classificationID) {
        this.classificationService = classificationService;
        this.classificationID = classificationID;
        this.personTournamentDataService = personTournamentDataService;
        classification = classificationService.get(classificationID).orElseThrow(RuntimeException::new);
        Set<Integer> tournamentsIdsSet = classification.getTablesIds();
        tournamentsIds = List.copyOf(tournamentsIdsSet);
        personTournamentDataList = personTournamentDataService.findDistinctByTournamentIdIn(tournamentsIds);
        tournamentList = tournamentService.findByIdIn(tournamentsIds);

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        grid.addColumn(classificationDataView -> {
            return classificationDataView.getFirstName() + " " + classificationDataView.getLastName();
        }).setHeader("Zawodnik").setAutoWidth(true);

        tournamentList.forEach(tournament -> {
            grid.addColumn(classificationDataView -> {
                return classificationDataView.getScoreMap().get(tournament.getId());
            }).setHeader(tournament.getUnit().getShortName()).setAutoWidth(true);
        });
        EnhancedColumn<ClassificationDataView> classificationDataViewEnhancedColumn = (EnhancedColumn<ClassificationDataView>) grid.addColumn(classificationDataView -> {
            return classificationDataView.getScoreMap().entrySet().stream().mapToInt(entry -> (int) entry.getValue()).sum();
        }).setHeader("Suma").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        List<GridSortOrder<ClassificationDataView>> sortBySum = new GridSortOrderBuilder<ClassificationDataView>().thenDesc(classificationDataViewEnhancedColumn).build();
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


        List<ClassificationDataView> classificationDataViewList = classificationService.getClassificationDataViewList(tournamentsIds, personTournamentDataList, classification);
        if (!classificationDataViewList.isEmpty()) grid.setItems(classificationDataViewList);

    }


}
