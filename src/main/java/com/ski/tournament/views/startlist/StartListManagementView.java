package com.ski.tournament.views.startlist;

import com.ski.tournament.core.SignStatus;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.util.*;
import java.util.stream.Collectors;

@PageTitle("startlist")
@Route(value = "startlist", layout = MainLayout.class)
@Secured("ROLE_Staff")
public class StartListManagementView extends HorizontalLayout implements BeforeEnterObserver  {

    private PersonTournamentDataService personTournamentDataService;

    private Set<PersonTournamentData> selectedContenders = new HashSet<>();
    private List<PersonTournamentData> availableContenders = new ArrayList<>();
    private StartListGrid twinColGrid;

    private VerticalLayout verticalLayout;
    private Button generateBtn;
    private Button generateOptionsBtn;
    private Button saveBtn;
    private Button clearBtn;

    private GenerateStartListPropertiesDialog generateStartListPropertiesDialog;
    private Integer minNr = 0;
    private Integer numberOfContenders;
    private Set<String> genders;
    private Integer tournamentID;

    public StartListManagementView(PersonTournamentDataService personTournamentDataService, Integer tournamentID) {

        this.personTournamentDataService = personTournamentDataService;
        this.verticalLayout = new VerticalLayout();
        this.tournamentID = tournamentID;
        availableContenders = fetchGridData(false,tournamentID);
        selectedContenders = Set.copyOf(fetchGridData(true,tournamentID));

        numberOfContenders = availableContenders.size();
       twinColGrid =
               (StartListGrid) new StartListGrid(availableContenders, "Lista startowa", personTournamentDataService, tournamentID)
                       .addSortableColumn(PersonTournamentData::getNrAsString,Comparator.comparing(PersonTournamentData::getNr) , "Numer" ,"keyColumn")
                       .addSortableColumn(PersonTournamentData::getPersonFirstName,Comparator.comparing(PersonTournamentData::getPersonFirstName) , "Imie" )
                       .addSortableColumn(PersonTournamentData::getPersonLastName,Comparator.comparing(PersonTournamentData::getPersonLastName) , "Nazwisko" )
                       .addSortableColumn(PersonTournamentData::getPersonGender,Comparator.comparing(PersonTournamentData::getPersonGender) , "Płeć" )
                       .withLeftColumnCaption("Dostępni zawodnicy")
                       .withRightColumnCaption("Dodani zawodnicy")
                       .withoutAddAllButton()
                       .withSizeFull()
                       .withDragAndDropSupport()
                       .selectRowOnClick();
        twinColGrid.setValue(selectedContenders);

        twinColGrid.getLeftGrid().getColumns().get(0).setVisible(false);
        twinColGrid.getLeftGrid().getDataProvider().refreshAll();
        twinColGrid.getRightGrid().getDataProvider().refreshAll();

        this.generateBtn = new Button("Generuj liste");
        this.generateOptionsBtn = new Button("Opcje generowania");
        this.saveBtn = new Button("Zapisz liste");
        this.clearBtn = new Button("Czyść liste");

        generateBtn.setWidthFull();

        generateBtn.addClickListener( e -> {
            if(availableContenders.isEmpty())
                new Notification("Nie znaleziono żadnego dostępnego zawodnika").open();
            else {
                twinColGrid.resetGrid();
                LinkedHashSet generatedStartList = personTournamentDataService.generateStartList(twinColGrid.getLeftGrid().getDataProvider().fetch(new Query<>()).collect(Collectors.toList()), numberOfContenders, genders);
                twinColGrid.updateSelectionAutomatically(generatedStartList, new HashSet(),minNr);
            }
        });
        generateOptionsBtn.setWidthFull();
        saveBtn.setWidthFull();

        saveBtn.addClickListener(e -> {

            availableContenders.forEach( personTournamentData -> personTournamentDataService.update(personTournamentData));
            selectedContenders.forEach( personTournamentData -> personTournamentDataService.update(personTournamentData));

        });

        generateOptionsBtn.addClickListener(e -> {
            generateStartListPropertiesDialog = new GenerateStartListPropertiesDialog();

            generateStartListPropertiesDialog.addOpenedChangeListener( ev -> {
                if(!ev.isOpened()) {
                    HashMap<String,Object> result = generateStartListPropertiesDialog.getResult();
                    if ((Boolean) result.get("OK")) {
                        minNr = result.containsKey("NR") && result.get("NR")!=null ?  (Integer) result.get("NR") - 1 : 0;
                        numberOfContenders = result.containsKey("NUMBER_OF_CONTENDERS") && result.get("NUMBER_OF_CONTENDERS") != null ? (Integer) result.get("NUMBER_OF_CONTENDERS") : availableContenders.size();
                        genders = result.containsKey("GENDER") && result.get("GENDER")!=null ? (Set<String>) result.get("GENDER") : new HashSet<>();
                    }
                }
            });

            generateStartListPropertiesDialog.open();
        });

        verticalLayout.add(generateBtn, generateOptionsBtn,saveBtn);
        verticalLayout.setWidth("20%");
        add(twinColGrid, verticalLayout);

        setSizeFull();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }

    private List<PersonTournamentData> fetchGridData(boolean nrNotNull, Integer tournamentId){
        List<PersonTournamentData> personTournamentDataList = new ArrayList<>();
        if(nrNotNull)
            personTournamentDataService.getAllPersonTournamentDataForTournamentAndSignStatus(tournamentId, SignStatus.ACCEPTED).forEach(personTournamentData -> {
                if(personTournamentData.getNr()!=null) personTournamentDataList.add(personTournamentData);
            });
        else personTournamentDataService.getAllPersonTournamentDataForTournamentAndSignStatus(tournamentId, SignStatus.ACCEPTED).forEach(personTournamentData -> {
            if(personTournamentData.getNr()==null) personTournamentDataList.add(personTournamentData);
        });
            return personTournamentDataList;
    }
}
