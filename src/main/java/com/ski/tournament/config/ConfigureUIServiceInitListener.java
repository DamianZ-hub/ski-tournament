package com.ski.tournament.config;

import com.ski.tournament.views.security.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

//    private void beforeEnter(BeforeEnterEvent event) {
//        if (!LoginView.class.equals(event.getNavigationTarget())
//                && !SecurityUtils.isUserLoggedIn()) {
//            event.rerouteTo(LoginView.class);
//        }
//    }

    private void beforeEnter(BeforeEnterEvent event) {
        if(!SecurityUtils.isAccessGranted(event.getNavigationTarget())) { // (1)
            if(SecurityUtils.isUserLoggedIn()) { // (2)
                event.rerouteToError(NotFoundException.class); // (3)
            } else {
                event.rerouteTo(LoginView.class); // (4)
            }
        }
    }
}