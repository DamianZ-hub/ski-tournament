package com.ski.tournament.views.tournaments;

import com.ski.tournament.core.CompetitionType;
import com.ski.tournament.service.*;
import com.ski.tournament.views.MainLayout;
import com.ski.tournament.views.startlist.StartListView;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsOneCompetitionTypeGrid;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsSponsorCupGrid;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsTeamCompetitionGrid;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsUnitMasteryGrid;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.ObjectUtils;

@PageTitle("Tabela")
@Route(value = "tournaments/:tournamentID?/tournamentScores/:competition-type?", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_User")
public class TournamentTableView extends VerticalLayout implements BeforeEnterObserver{

    private String competitionType;
    private String tournamentID;
    private VerticalLayout gridView;
    private VerticalLayout teamGridView;
    private StartListView startListView;
    private MenuBar menuBar = new MenuBar();
    private SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService;
    private SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;
    private SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;
    private SingleCompetitionsTeamCompetitionsDataService singleCompetitionsTeamCompetitionsDataService;
    private PersonTournamentDataService personTournamentDataService;

    public TournamentTableView(@Autowired SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService,
                               @Autowired SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService,
                               @Autowired SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService,
                               @Autowired PersonTournamentDataService personTournamentDataService,
                               @Autowired SingleCompetitionsTeamCompetitionsDataService singleCompetitionsTeamCompetitionsDataService) {
        this.singleCompetitionsSponsorCupDataService = singleCompetitionsSponsorCupDataService;
        this.singleCompetitionsUnitMasteryDataService = singleCompetitionsUnitMasteryDataService;
        this.singleCompetitionsOneCompetitionTypeDataService = singleCompetitionsOneCompetitionTypeDataService;
        this.singleCompetitionsTeamCompetitionsDataService = singleCompetitionsTeamCompetitionsDataService;
        this.personTournamentDataService = personTournamentDataService;

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        menuBar.addItem("Lista startowa", e -> {
            gridView.setVisible(false);
            if(!ObjectUtils.nullSafeEquals(teamGridView,null)) teamGridView.setVisible(false);
            startListView.setVisible(true);
        });
        menuBar.addItem("Tabela wyników", e -> {
            startListView.setVisible(false);
            if(!ObjectUtils.nullSafeEquals(teamGridView,null)) teamGridView.setVisible(false);
            gridView.setVisible(true);
        });

       add(menuBar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        competitionType = beforeEnterEvent.getRouteParameters().get("competition-type").
                orElseThrow();
        tournamentID = beforeEnterEvent.getRouteParameters().get("tournamentID").
                orElseThrow();
        setTableType();
        startListView = new StartListView(personTournamentDataService,Integer.parseInt(tournamentID));
        startListView.setVisible(false);
        if(!ObjectUtils.nullSafeEquals(teamGridView,null)) teamGridView.setVisible(false);
        add(startListView);
    }

    private void setTableType(){
        switch(CompetitionType.valueOfLabel12(competitionType).label2){
            case "SingleCompetitionsSponsorCup":
                gridView = new SingleCompetitionsSponsorCupGrid(singleCompetitionsSponsorCupDataService,Integer.parseInt(tournamentID));
                add(gridView);
                teamGridView = new SingleCompetitionsTeamCompetitionGrid(singleCompetitionsTeamCompetitionsDataService,Integer.parseInt(tournamentID),competitionType);
                add(teamGridView);
                menuBar.addItem("Tabela drużynowa", e -> {
                    gridView.setVisible(false);
                    startListView.setVisible(false);
                    teamGridView.setVisible(true);
                });
                break;
            case "SingleCompetitionsUnitMastery":
                gridView = new SingleCompetitionsUnitMasteryGrid(singleCompetitionsUnitMasteryDataService,Integer.parseInt(tournamentID));
                add(gridView);
                break;
            case "SingleCompetitionsOneCompetitionType":
                gridView = new SingleCompetitionsOneCompetitionTypeGrid(singleCompetitionsOneCompetitionTypeDataService,Integer.parseInt(tournamentID));
                add(gridView);
                teamGridView = new SingleCompetitionsTeamCompetitionGrid(singleCompetitionsTeamCompetitionsDataService,Integer.parseInt(tournamentID),competitionType);
                add(teamGridView);
                menuBar.addItem("Tabela drużynowa", e -> {
                    gridView.setVisible(false);
                    startListView.setVisible(false);
                    teamGridView.setVisible(true);
                });
                break;
            default:
                new Notification("Nieprawidłowy typ tabeli").open();
                break;
        }
    }
}
