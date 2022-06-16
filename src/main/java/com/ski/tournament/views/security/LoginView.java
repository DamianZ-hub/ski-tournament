package com.ski.tournament.views.security;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.Collections;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    public static final String ROUTE = "login";

    private LoginForm login;


    public LoginView(){
        login = new LoginForm();
        VerticalLayout layout = getContent();
        layout.add(login);
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        login.setAction("login");
        layout.add(new RouterLink("Register", RegisterView.class));

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true);
        }
    }
}