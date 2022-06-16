package com.ski.tournament.views.security;

import com.ski.tournament.model.Unit;
import com.ski.tournament.service.PersonService;
import com.ski.tournament.service.UnitService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
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
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Route("register")
public class RegisterView extends Composite {

    private final PersonService personService;
    private final UnitService unitService;

    @Autowired
    public RegisterView(PersonService personService, UnitService unitService) {
        this.personService = personService;
        this.unitService = unitService;
    }

    @Override
    protected Component initContent() {
        EmailField username = new EmailField("Email");

        PasswordField password1 = new PasswordField("Hasło");
        password1.setRequired(true);
        PasswordField password2 = new PasswordField("Potwórz hasło");
        password2.setRequired(true);
        DatePicker birthDatePicker = new DatePicker("Data urodzenia");
        birthDatePicker.setRequired(true);
        birthDatePicker.setPlaceholder("Data");

        Select<String> genderSelect = new Select<>();
        genderSelect.setLabel("Płeć");
        genderSelect.setItems("Mezczyzna","Kobieta");

        Select<Unit> unitSelect = new Select<>();
        unitSelect.setLabel("Jednostka organizacyjna");
        unitSelect.setItems(fetchAllUnits());
        unitSelect.setItemLabelGenerator(Unit::getFullName);

        TextField firstName = new TextField("Imie");
        firstName.setRequired(true);

        TextField lastName = new TextField("Nazwisko");
        lastName.setRequired(true);

       TextField phone = new TextField("Numer telefonu");
       phone.setRequired(false);

        H2 title = new H2("Rejestracja");

        Button submitBtn = new Button("Wyślij", event -> register(
                username.getValue(),
                password1.getValue(),
                password2.getValue(),
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
                password1,
                unitSelect,
                password2,
                birthDatePicker,
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

    private void register(String username, String password1, String password2, String gender, String firstName, String lastName, Unit unit, LocalDate birthDate, String phone) {
        if (username.trim().isEmpty()) {
            Notification.show("Podaj email");
        } else if (password1.isEmpty()) {
            Notification.show("Podaj hasło");
        } else if (!password1.equals(password2)) {
            Notification.show("Hasła nie są identyczne");
        } else if (gender==null || firstName.trim().isEmpty() || lastName.trim().isEmpty() || unit == null || birthDate == null) {
            Notification.show("Wszystkie wymagane pola muszą być wypełnione");
        } else {
           personService.register(username, password1, gender, unit, firstName, lastName, birthDate, phone);
            Dialog dialog = new Dialog();
            dialog.getElement().setAttribute("aria-label", "Gratulacje");
            dialog.add("Na twój email został wysłany link aktywacyjny w celu dokończenia rejestracji");
            dialog.open();
        }
    }

    private List<Unit> fetchAllUnits() {
        List<Unit> unitsList= unitService.getUnits();
        if(!unitsList.isEmpty()) return unitsList;

        new Notification("Nie znaleziono jednostek organizacyjnych").open();
        return unitsList;
    }


}
