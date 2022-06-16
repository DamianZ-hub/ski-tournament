package com.ski.tournament.views.startlist;

import com.ski.tournament.core.TextFilterField;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.service.PersonTournamentDataService;
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


public class StartListView  extends VerticalLayout implements AfterNavigationObserver {


        private EnhancedGrid<PersonTournamentData> grid = new EnhancedGrid<>();

        private Integer tournamentID;

        private PersonTournamentData personTournamentData;

        private PersonTournamentDataService personTournamentDataService;

        public StartListView(PersonTournamentDataService personTournamentDataService, Integer tournamentID) {
            this.personTournamentDataService = personTournamentDataService;
            this.tournamentID = tournamentID;
            addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

            EnhancedColumn<PersonTournamentData> nrColumn = grid.addColumn(PersonTournamentData::getNr).setHeader("Numer",new TextFilterField());
            grid.addColumn(personTournamentData -> {
                return personTournamentData.getPerson().getFirstName();
            }).setHeader("Imie",new TextFilterField()).setAutoWidth(true);
            grid.addColumn(personTournamentData -> {
                return personTournamentData.getPerson().getLastName();
            }).setHeader("Nazwisko",new TextFilterField()).setAutoWidth(true);

            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                    GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

            List<GridSortOrder<PersonTournamentData>> sortByNr = new GridSortOrderBuilder<PersonTournamentData>().thenAsc(nrColumn).build();
            grid.sort(sortByNr);

            grid.setSelectionMode(Grid.SelectionMode.SINGLE);

            add(grid);
        }


        private void refreshGrid() {
            grid.select(null);
            grid.getDataProvider().refreshAll();
        }


        @Override
        public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
            List<PersonTournamentData> personTournamentDataList = personTournamentDataService.findAllByTournamentIdAndNrNotNull(tournamentID);
            if(!personTournamentDataList.isEmpty()) grid.setItems(personTournamentDataList);
                else new Notification("Lista startowa jest pusta!");
        }


}

