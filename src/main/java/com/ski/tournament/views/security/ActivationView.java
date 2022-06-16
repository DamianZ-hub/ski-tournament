package com.ski.tournament.views.security;

import com.ski.tournament.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Route("activate")
public class ActivationView extends Composite<Component> implements BeforeEnterObserver {

    private VerticalLayout layout;

    private final PersonService personService;

    @Autowired
    public ActivationView(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
            String code = params.get("code").get(0);
            personService.activate(code);
            layout.add(
                    new Text("Konto zostało aktywowane"),
                    new RouterLink("Login", LoginView.class)
            );
        } catch (PersonService.AuthException e) {
            layout.add(new Text("Nieprawidłowy link"));
        }
    }

    @Override
    protected Component initContent() {
        layout = new VerticalLayout();
        return layout;
    }
}