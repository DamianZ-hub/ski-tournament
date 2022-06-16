package com.ski.tournament.views.classification;

import com.ski.tournament.core.ClassificationType;
import com.ski.tournament.service.ClassificationService;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.service.TournamentService;
import com.ski.tournament.views.MainLayout;
import com.ski.tournament.views.classificationGrid.GeneralClassificationKUAZSCSiRCupGrid;
import com.ski.tournament.views.classificationGrid.GeneralClassificationRectorCupGrid;
import com.ski.tournament.views.classificationGrid.GeneralTeamClassificationGrid;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@PageTitle("Tabela")
@Route(value = "classifications/:classificationID?/:classification-type?", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_User")
public class ClassificationsTableView extends VerticalLayout implements BeforeEnterObserver{

    private String classificationType;
    private String classificationID;
    private VerticalLayout gridView;

    private ClassificationService classificationService;
    private PersonTournamentDataService personTournamentDataService;
    private TournamentService tournamentService;

    public ClassificationsTableView(@Autowired ClassificationService classificationService,
                                    @Autowired PersonTournamentDataService personTournamentDataService,
                                    @Autowired TournamentService tournamentService) {
        this.classificationService = classificationService;
        this.personTournamentDataService = personTournamentDataService;
        this.tournamentService = tournamentService;

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        classificationType = beforeEnterEvent.getRouteParameters().get("classification-type").
                orElseThrow();
        classificationID = beforeEnterEvent.getRouteParameters().get("classificationID").
                orElseThrow();
        setTableType();
    }

    private void setTableType(){
        switch(ClassificationType.valueOfLabel12(classificationType).label2){
            case "GeneralClassificationRectorCup":
                gridView = new GeneralClassificationRectorCupGrid(classificationService, personTournamentDataService, tournamentService, Integer.parseInt(classificationID));
                add(gridView);
                break;
            case "GeneralClassificationKUAZSCSiRCup":
                gridView = new GeneralClassificationKUAZSCSiRCupGrid(classificationService, personTournamentDataService, tournamentService, Integer.parseInt(classificationID));
                add(gridView);
                break;
            case "GeneralTeamClassification":
                gridView = new GeneralTeamClassificationGrid(classificationService, Integer.parseInt(classificationID));
                add(gridView);
                break;
            default:
                new Notification("Nieprawidłowy typ tabeli").open();
                break;
        }
    }
}
