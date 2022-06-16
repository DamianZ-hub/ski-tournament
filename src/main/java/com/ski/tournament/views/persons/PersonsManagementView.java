package com.ski.tournament.views.persons;

import com.ski.tournament.config.UserRole;
import com.ski.tournament.core.ManagementView;
import com.ski.tournament.model.Person;
import com.ski.tournament.model.Unit;
import com.ski.tournament.service.PersonService;
import com.ski.tournament.service.UnitService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("Zarządzanie użytkownikami")
@Route(value = "users-management/:userID?/:action?(edit)", layout = MainLayout.class)
@Secured("ROLE_Admin")
@Uses(Icon.class)
public class PersonsManagementView extends ManagementView<Person, PersonService> {

    private UnitService unitService;
    private List<Person> personList;
    private List<Unit> unitList;

    private TextField firstName;
    private TextField lastName;
    private Select<String> genderSelect;
    private DatePicker birthDatePicker;
    private Select<Unit> unitSelect;
    private EmailField email;
    private TextField phone;

    private MultiComboBox<UserRole> roleSelect;
    private Set<UserRole> roles;

    @Autowired
    public PersonsManagementView(PersonService personService, UnitService unitService) {
        super(personService, Person.class, "user");

        this.unitService = unitService;
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        initialize();
    }


    @Override
    public void initialize() {
        this.setSizeFull();

        personList = fetchAllPersons();
        unitList = fetchAllUnits();

        unitSelect.setItems(unitList);

        roles = new HashSet<>();
        roles.add(UserRole.ROLE_User);
        roles.add(UserRole.ROLE_Staff);
        roles.add(UserRole.ROLE_Admin);
        roleSelect.setItems(roles);

    }


    @Override
    public Component[] addFieldsToEditorPanel() {

        firstName = new TextField("Imie");
        firstName.setRequired(true);

        lastName = new TextField("Nazwisko");
        lastName.setRequired(true);

        genderSelect = new Select<>();
        genderSelect.setLabel("Płeć");
        genderSelect.setItems("Mezczyzna","Kobieta");

        birthDatePicker = new DatePicker("Data urodzenia");
        birthDatePicker.setRequired(true);
        birthDatePicker.setPlaceholder("Data");

        unitSelect = new Select<>();
        unitSelect.setLabel("Jednostka organizacyjna");
        unitSelect.setItemLabelGenerator(Unit::getFullName);

        email = new EmailField("Email");
        email.setClearButtonVisible(true);
        email.setErrorMessage("Podaj poprawny adres email");

        phone = new TextField("Numer telefonu");
        phone.setRequired(false);

        roleSelect = new MultiComboBox<>();
        roleSelect.setItemLabelGenerator(UserRole::getLabel1);
        roleSelect.setLabel("Uprawnienia");

        Component[] fields = new Component[]{firstName, lastName, genderSelect, birthDatePicker, unitSelect, email, phone, roleSelect};
        return fields;
    }

    @Override
    public void addColumnsToGrid() {

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        grid.addColumn(person -> person.getFirstName()).setHeader("Imie").setAutoWidth(true);
        grid.addColumn(person -> person.getLastName()).setHeader("Nazwisko").setAutoWidth(true);
        grid.addColumn(person -> person.getGender()).setHeader("Płeć").setAutoWidth(true);
        grid.addColumn(person -> person.getDateOfBirth()).setHeader("Data urodzenia").setAutoWidth(true);
        grid.addColumn(person -> person.getUnit().getShortName()).setHeader("Jednostka organizacyjna").setAutoWidth(true);
        grid.addColumn(person -> person.getUsername()).setHeader("Email").setAutoWidth(true);
        grid.addColumn(person -> person.getPhone()).setHeader("Numer telefonu").setAutoWidth(true);
        grid.addColumn(person -> person.getAuthorities()).setHeader("Uprawnienia").setAutoWidth(true);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }

    @Override
    protected void configureBinder() {
        binder = new BeanValidationBinder<>(Person.class);

        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(genderSelect, "gender");
        binder.forField(birthDatePicker).bind("dateOfBirth");
        binder.bind(unitSelect,"unit");
        binder.bind(email, "username");
        binder.bind(phone, "phone");
        binder.bind(roleSelect, "authorities");


    }


    private List<Person> fetchAllPersons() {
        List<Person> personList = service.getPersons();
        if (!personList.isEmpty()) return personList;

        new Notification("Nie znaleziono użytkowników").open();
        return personList;
    }
    private List<Unit> fetchAllUnits() {
        List<Unit> unitsList= unitService.getUnits();
        if(!unitsList.isEmpty()) return unitsList;

        new Notification("Nie znaleziono jednostek organizacyjnych").open();
        return unitsList;
    }

}

