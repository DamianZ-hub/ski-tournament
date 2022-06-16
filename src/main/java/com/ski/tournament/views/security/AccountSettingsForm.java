package com.ski.tournament.views.security;


import com.ski.tournament.model.Person;
import com.ski.tournament.model.Unit;
import com.ski.tournament.service.PersonService;
import com.ski.tournament.service.UnitService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@PageTitle("Ustawienia konta")
@Route(value = "account-settings/", layout = MainLayout.class)
@Secured("ROLE_User")
public class AccountSettingsForm extends Composite implements BeforeEnterObserver {


    private final PersonService personService;
    private final UnitService unitService;
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private Person currentUser;
    private EmailField username;
    private PasswordField password1;
    private PasswordField password2;
    private PasswordField oldPassword;
    private DatePicker birthDatePicker;
    private Select<String> genderSelect;
    private Select<Unit> unitSelect;
    private TextField firstName;
    private TextField lastName;
    private TextField phone;


    @Autowired
    public AccountSettingsForm(PersonService personService, UnitService unitService) {
        this.personService = personService;
        this.unitService = unitService;
        currentUser = PersonService.getCurrentLoggedUser();

    }

    @Override
    protected Component initContent() {

        username = new EmailField("Email");
        username.setValue(currentUser.getUsername());

        password1 = new PasswordField("Hasło");
        password2 = new PasswordField("Potwórz hasło");
        oldPassword = new PasswordField("Obecne hasło:");
        birthDatePicker = new DatePicker("Data urodzenia");
        birthDatePicker.setPlaceholder("Data");
        birthDatePicker.setValue(currentUser.getDateOfBirth());

        genderSelect = new Select<>();
        genderSelect.setLabel("Płeć");
        genderSelect.setItems("Mezczyzna","Kobieta");
        genderSelect.setValue(currentUser.getGender());

        unitSelect = new Select<>();
        unitSelect.setLabel("Jednostka organizacyjna");
        unitSelect.setItems(fetchAllUnits());
        unitSelect.setItemLabelGenerator(Unit::getFullName);
        unitSelect.setValue(currentUser.getUnit());

        firstName = new TextField("Imie");
        firstName.setValue(currentUser.getFirstName());

        lastName = new TextField("Nazwisko");
        lastName.setValue(currentUser.getLastName());

        phone = new TextField("Numer telefonu");
        phone.setValue(currentUser.getPhone()!=null ? currentUser.getPhone() : "");

        H2 title = new H2("Rejestracja");

        Button submitBtn = new Button("Wyślij", event -> submit(
                username.getValue(),
                password1.getValue(),
                password2.getValue(),
                oldPassword.getValue(),
                genderSelect.getValue(),
                firstName.getValue(),
                lastName.getValue(),
                unitSelect.getValue(),
                birthDatePicker.getValue(),
                phone.getValue()));
        submitBtn.getStyle().set("margin-top","40px");

        FormLayout formLayout = new FormLayout(
                title,
                username,
                genderSelect,
                oldPassword,
                unitSelect,
                password1,
                birthDatePicker,
                password2,
                firstName,
                lastName,
                phone,
                submitBtn
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2)
        );
        formLayout.setColspan(title,2);
        formLayout.setColspan(submitBtn,2);

        formLayout.getStyle().set("align-self","auto");
        formLayout.setMaxWidth("30%");
        VerticalLayout layout = new VerticalLayout(formLayout);
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        return layout;
    }

    private void submit(String username, String password1, String password2, String oldPassword, String gender, String firstName, String lastName, Unit unit, LocalDate birthDate, String phone) {
        HashMap<String,Object> data = new HashMap<>();

        boolean isOk = true;


        if (!username.trim().isEmpty() && !currentUser.getUsername().equals(username)) {
            if(personService.checkIfExistsPersonByUsername(username)) {
                Notification.show("Istnieje już konto posiadające ten email");
                isOk = false;
            }
            if(!encoder.matches(oldPassword,currentUser.getPassword())) {
                Notification.show("Aby zmienić email musisz podać hasło");
                isOk = false;
            }
            else data.put("USERNAME",username);
        }

        if(!password1.isEmpty() && !password2.isEmpty() && !oldPassword.isEmpty()){
            if(!encoder.matches(oldPassword,currentUser.getPassword())){
                Notification.show("Hasło nieprawidłowe");
                isOk = false;
            } else if (!password1.equals(password2)) {
            Notification.show("Hasła nie są identyczne");
                isOk = false;
            }
            else data.put("NEWPASSWORD",password1);
        }

        if(!gender.isEmpty() && (gender.equals("Mezczyzna") || gender.equals("Kobieta")) && !currentUser.getGender().equals(gender)) {
            data.put("GENDER",gender);
        }

        if(!firstName.isEmpty() && !currentUser.getFirstName().equals(firstName)) {
            data.put("FIRSTNAME",firstName);
        }

        if(!lastName.isEmpty() && !currentUser.getLastName().equals(lastName)) {
            data.put("LASTNAME",lastName);
        }

        if(unit!=null && !currentUser.getUnit().equals(unit)) {
            data.put("UNIT",unit);
        }

        if(birthDate!=null && !currentUser.getDateOfBirth().equals(birthDate)) {
            data.put("BIRTHDATE",birthDate);
        }

        if(phone != null && !StringUtils.equals(phone,currentUser.getPhone())){
            data.put("PHONE",phone);
        }

        if(data.size() > 0 && currentUser.getId()!=null && isOk) {
            data.put("ID",currentUser.getId());
            HashMap<String,Object> result = personService.editAccount(data,currentUser);
            Dialog dialog = new Dialog();
            dialog.add( (String) result.get("DETAILS"));
            dialog.open();
            dialog.addOpenedChangeListener(e -> {
                    UI.getCurrent().getPage().setLocation("/logout");
            });
        }

    }

    private List<Unit> fetchAllUnits() {
        List<Unit> unitsList= unitService.getUnits();
        if(!unitsList.isEmpty()) return unitsList;

        new Notification("Nie znaleziono jednostek organizacyjnych").open();
        return unitsList;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    }
}
