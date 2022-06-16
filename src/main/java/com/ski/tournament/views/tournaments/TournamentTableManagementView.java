package com.ski.tournament.views.tournaments;

import com.ski.tournament.core.CompetitionType;
import com.ski.tournament.service.*;
import com.ski.tournament.views.MainLayout;
import com.ski.tournament.views.startlist.StartListManagementView;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsOneCompetitionTypeGridManagement;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsSponsorCupGridManagement;
import com.ski.tournament.views.tournamentgrid.SingleCompetitionsUnitMasteryGridManagement;
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
@Route(value = "tournaments-management/:tournamentID?/tournamentScores/:competition-type?/:tournament-scoreID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_Staff")
public class TournamentTableManagementView extends VerticalLayout implements BeforeEnterObserver{

    private String competitionType;
    private String tournamentID;
    private VerticalLayout gridView;
    private StartListManagementView startListManagementView;

    private SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService;
    private SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;
    private SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;
    private PersonTournamentDataService personTournamentDataService;
    private UnitService unitService;

    public TournamentTableManagementView(@Autowired SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService,
                                         @Autowired SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService,
                                         @Autowired SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService,
                                         @Autowired PersonTournamentDataService personTournamentDataService,
                                         @Autowired UnitService unitService) {
        this.singleCompetitionsSponsorCupDataService = singleCompetitionsSponsorCupDataService;
        this.singleCompetitionsUnitMasteryDataService = singleCompetitionsUnitMasteryDataService;
        this.singleCompetitionsOneCompetitionTypeDataService = singleCompetitionsOneCompetitionTypeDataService;
        this.personTournamentDataService = personTournamentDataService;
        this.unitService = unitService;




        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");
        MenuBar menuBar = new MenuBar();

        menuBar.addItem("Modyfikuj listę startową", e -> {
            gridView.setVisible(false);
            startListManagementView.setVisible(true);
        });
        menuBar.addItem("Modyfikuj tabelę wyników", e -> {
            startListManagementView.setVisible(false);
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
        if(ObjectUtils.nullSafeEquals(gridView,null) || !gridView.isAttached())
            setTableType();
        if(ObjectUtils.nullSafeEquals(startListManagementView,null) || !startListManagementView.isAttached()) {
            startListManagementView = new StartListManagementView(personTournamentDataService, Integer.parseInt(tournamentID));
            startListManagementView.setVisible(false);
            add(startListManagementView);
        }
    }

    private void setTableType(){
        switch(CompetitionType.valueOfLabel12(competitionType).label2){
            case "SingleCompetitionsSponsorCup":
                gridView = new VerticalLayout();
                gridView.add(new SingleCompetitionsSponsorCupGridManagement(singleCompetitionsSponsorCupDataService,unitService,personTournamentDataService, Integer.parseInt(tournamentID),CompetitionType.SINGLE_COMPETITIONS_SPONSOR_CUP));
                gridView.setSizeFull();
                add(gridView);
                break;
            case "SingleCompetitionsUnitMastery":
                gridView = new VerticalLayout();
                gridView.add(new SingleCompetitionsUnitMasteryGridManagement(singleCompetitionsUnitMasteryDataService, unitService, personTournamentDataService, Integer.parseInt(tournamentID), CompetitionType.SINGLE_COMPETITIONS_UNIT_MASTERY));
                gridView.setSizeFull();
                add(gridView);
                break;
            case "SingleCompetitionsOneCompetitionType":
                gridView = new VerticalLayout();
                gridView.add(new SingleCompetitionsOneCompetitionTypeGridManagement(singleCompetitionsOneCompetitionTypeDataService,unitService,personTournamentDataService, Integer.parseInt(tournamentID),CompetitionType.SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE));
                gridView.setSizeFull();
                add(gridView);
                break;
            default:
                new Notification("Nieprawidłowy typ tabeli").open();
                break;
        }
    }
}
