package com.ski.tournament.views.tournaments;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.ski.tournament.views.MainLayout;

@PageTitle("Tournaments")
@Route(value = "tournaments", layout = MainLayout.class)
public class TournamentsView extends Div {

    public TournamentsView() {
        addClassName("tournaments-view");
        add(new Text("Content placeholder"));
    }

}
