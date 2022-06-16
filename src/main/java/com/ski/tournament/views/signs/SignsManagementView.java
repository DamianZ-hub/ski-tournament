package com.ski.tournament.views.signs;

import com.ski.tournament.core.ManagementView;
import com.ski.tournament.core.SignStatus;
import com.ski.tournament.model.Person;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.model.Tournament;
import com.ski.tournament.service.PersonService;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.service.TournamentService;
import com.ski.tournament.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

@PageTitle("Zarządzanie zapisami")
@Route(value = "signs-management/:signID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_Staff")
public class SignsManagementView extends ManagementView<PersonTournamentData, PersonTournamentDataService> {

    private PersonService personService;
    private TournamentService tournamentService;
    private List<Person> personList;
    private List<Tournament> tournamentList;

    private Select<Person> personSelect;
    private Select<Tournament> tournamentSelect;
    private Select<SignStatus> statusSelect;


    @Autowired
    public SignsManagementView(PersonTournamentDataService personTournamentDataService,
                               PersonService personService,
                               TournamentService tournamentService) {
        super(personTournamentDataService, PersonTournamentData.class, "sign");

        this.personService = personService;
        this.tournamentService = tournamentService;
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        initialize();
    }


    @Override
    public void initialize() {
        this.setSizeFull();

        personList = fetchAllPersons();
        tournamentList = fetchAllTournaments();

        personSelect.setItems(personList);
        tournamentSelect.setItems(tournamentList);
    }


    @Override
    public Component[] addFieldsToEditorPanel() {

        personSelect = new Select<>();
        personSelect.setLabel("Zawodnik");
        personSelect.setItemLabelGenerator(Person::getFirstNameLastNameAndUnit);

        tournamentSelect = new Select<>();
        tournamentSelect.setLabel("Turniej");
        tournamentSelect.setItemLabelGenerator(Tournament::getTournamentData);

        statusSelect = new Select<>();
        statusSelect.setLabel("Status zapisu");
        statusSelect.setItems(SignStatus.ACCEPTED,
                SignStatus.PENDING,
                SignStatus.REJECTED);
        statusSelect.setItemLabelGenerator(SignStatus::getLabel1);

        Component[] fields = new Component[]{personSelect,tournamentSelect,statusSelect};
        return fields;
    }

    @Override
    public void addColumnsToGrid() {

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        grid.addColumn(personTournamentData -> personTournamentData.getPerson().getFirstNameLastNameAndUnit()).setHeader("Zawodnik").setAutoWidth(true);
        grid.addColumn(personTournamentData -> personTournamentData.getTournament().getTournamentData()).setHeader("Turniej").setAutoWidth(true);
        grid.addColumn(personTournamentData -> personTournamentData.getSignStatus().label1).setHeader("Status").setAutoWidth(true);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }

    @Override
    protected void configureBinder() {
        binder = new BeanValidationBinder<>(PersonTournamentData.class);

        binder.forField(statusSelect).bind("signStatus");
        binder.bind(personSelect, "person");
        binder.bind(tournamentSelect, "tournament");
    }

    private List<Person> fetchAllPersons() {
        List<Person> personList = personService.getPersons();
        if (!personList.isEmpty()) return personList;
        new Notification("Nie znaleziono użytkowników").open();
        return personList;
    }

    private List<Tournament> fetchAllTournaments() {
        List<Tournament> tournamentList = tournamentService.getTournaments();
        if (!tournamentList.isEmpty()) return tournamentList;

        new Notification("Nie znaleziono turniejów").open();
        return tournamentList;
    }

}

