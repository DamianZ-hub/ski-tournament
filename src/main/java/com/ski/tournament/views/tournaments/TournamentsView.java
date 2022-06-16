package com.ski.tournament.views.tournaments;

import com.ski.tournament.core.SignStatus;
import com.ski.tournament.core.TournamentStatus;
import com.ski.tournament.model.Person;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.model.Tournament;
import com.ski.tournament.service.PersonService;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.service.TournamentService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@PageTitle("Zawody")
@Route(value = "tournaments", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Secured("ROLE_User")
public class TournamentsView extends Div implements AfterNavigationObserver {


    Grid<Tournament> grid = new Grid<>();

    private final TournamentService tournamentService;
    private final PersonTournamentDataService personTournamentDataService;
    private Person currentUser;
    private ToggleButton toggle;

    @Autowired
    public TournamentsView(TournamentService tournamentService, PersonTournamentDataService personTournamentDataService) {
        this.tournamentService = tournamentService;
        this.personTournamentDataService = personTournamentDataService;
        addClassName("tournaments-view");
        setSizeFull();
        toggle = new ToggleButton();
        toggle.setLabel("Pokazuj przeszłe zawody:");
        toggle.addClassName("card");
        toggle.setWidthFull();
        toggle.setValue(false);
        toggle.addValueChangeListener(evt -> setPastAndCanceledTournamentsVisible(evt.getValue()));


        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        Grid.Column<Tournament> column = grid.addComponentColumn(tournament -> createCard(tournament)).setComparator(
                (t1, t2) -> t1.getDateTime().compareTo(t2.getDateTime()));
        List<GridSortOrder<Tournament>> sortBySum = new GridSortOrderBuilder<Tournament>().thenDesc(column).build();
        grid.sort(sortBySum);

        add(toggle,grid);
    }

    private HorizontalLayout createCard(Tournament tournament) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        HorizontalLayout horizontalLayoutStatus = new HorizontalLayout();
        horizontalLayoutStatus.setAlignItems(FlexComponent.Alignment.CENTER);

        TournamentStatus tournamentStatus = tournament.getStatus();
        Label status = assignStyleToTournamentStatusLabel(tournamentStatus);
        horizontalLayoutStatus.add(status);
        horizontalLayoutStatus.setWidth("20%");

        HorizontalLayout horizontalLayoutDateTime = new HorizontalLayout();
        horizontalLayoutDateTime.setAlignItems(FlexComponent.Alignment.CENTER);
        Span date = new Span(String.valueOf(tournament.getDateTime().toLocalDate()));
        date.addClassName("date");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US);
        LocalTime localTime = tournament.getDateTime().toLocalTime();
        Span time = new Span(formatter.format(localTime));
        time.addClassName("date");
        Span description = new Span(tournament.getDescription());
        description.addClassName("description");
        Span patron = new Span(tournament.getUnit().getShortName());
        patron.addClassName("description");
        horizontalLayoutDateTime.add(date,time);
        horizontalLayoutDateTime.addClassName("date-time");

        Span place = new Span(tournament.getPlace().getTitle());
        place.setClassName("description");

        Span competition = new Span(tournament.getCompetition().label1.toString());
        competition.addClassName("competition");

        HorizontalLayout horizontalLayoutDescription = new HorizontalLayout();
        horizontalLayoutDescription.addAndExpand(place,competition,description,patron);
        horizontalLayoutDescription.setAlignItems(FlexComponent.Alignment.CENTER);


        HorizontalLayout horizontalLayoutSign = new HorizontalLayout();
        Icon signStatusIcon = assignSignStatusIcon(tournament.getId());

        Button sign = new Button();
        boolean alreadySigned = personTournamentDataService.checkIfExistsPersonTournamentDataByTournamentIdAndPersonId(tournament.getId(),currentUser.getId());
        SignStatus signStatus = personTournamentDataService.getSignStatusOfPersonTournamentDataByTournamentIdAndPersonId(tournament.getId(),currentUser.getId());

        sign.setEnabled(signStatus!=SignStatus.REJECTED && tournament.getStatus().equals(TournamentStatus.INCOMING));
        String txt = alreadySigned ? "anuluj" : "dolacz";
        sign.setText(txt);
        sign.addClickListener(e ->  {
                    sign(tournament, alreadySigned);
                });
        sign.setWidth("100px");
        Button table = new Button("tabela");
        table.addClickListener(e ->
                table.getUI().ifPresent(ui ->
                        ui.navigate(
                                TournamentTableView.class,
                                new RouteParameters( new HashMap<String,String>(){{
                                    put("competition-type",tournament.getCompetitionType().label2);
                                    put("tournamentID",tournament.getId().toString()) ;
                                }})))
                        );
        table.setWidth("100px");
        horizontalLayoutSign.add(signStatusIcon,sign,table);
        horizontalLayoutSign.setAlignItems(FlexComponent.Alignment.CENTER);

        card.addAndExpand(horizontalLayoutStatus,horizontalLayoutDateTime,horizontalLayoutDescription,horizontalLayoutSign);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        return card;
    }

    private void sign(Tournament tournament, boolean alreadySigned) {
        try {
            Dialog dialog = new Dialog();

            HashMap<String, Object> result;
            if (!alreadySigned) {
                result = personTournamentDataService.applyForTournament(tournament, currentUser);
                String dialogTxt = (String) result.get("DETAILS");
                dialog.add(dialogTxt);
            } else {
                result = personTournamentDataService.unapplyForTournament(tournament, currentUser);
                String dialogTxt = (String) result.get("DETAILS");
                dialog.add(dialogTxt);
            }
            dialog.open();
            grid.getDataProvider().refreshAll();
        }
        catch (Exception e) {
            Dialog dialog = new Dialog();
            dialog.add(e.getMessage());
            dialog.open();
        }
    }

    private Label assignStyleToTournamentStatusLabel(TournamentStatus tournamentStatus) {
        Label status = new Label(tournamentStatus.getLabel());
        status.addClassName("status");
        if (tournamentStatus.equals(TournamentStatus.ONGOING)) {
            status.getStyle().set("background-color","orange");
            status.getStyle().set("border-color","orange");
        }
        else if (tournamentStatus.equals(TournamentStatus.INCOMING)) {
            status.getStyle().set("background-color","green");
            status.getStyle().set("border-color","green");
        }
        else {
            status.getStyle().set("background-color","red");
            status.getStyle().set("border-color","red");
        }
        status.setWidthFull();
        return status;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        currentUser = PersonService.getCurrentLoggedUser();
        setPastAndCanceledTournamentsVisible(toggle.getValue());

    }

    private Icon assignSignStatusIcon(Integer tournamentID) {
        PersonTournamentData personTournamentData = personTournamentDataService.findOneByTournamentIdAndPersonId(tournamentID,currentUser.getId());
        Icon signStatusIcon;
        if(personTournamentData==null) {
            signStatusIcon = new Icon(VaadinIcon.CIRCLE_THIN);
            signStatusIcon.setColor("grey");
        }
        else if(personTournamentData.getSignStatus().equals(SignStatus.ACCEPTED)) {
            signStatusIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
            signStatusIcon.setColor("green");
        }
        else if(personTournamentData.getSignStatus().equals(SignStatus.PENDING)) {
            signStatusIcon = new Icon(VaadinIcon.TIME_FORWARD);
            signStatusIcon.setColor("orange");
        }
        else {
            signStatusIcon = new Icon(VaadinIcon.CLOSE_CIRCLE);
            signStatusIcon.setColor("red");
        }
        signStatusIcon.addClassName("icon");
        return signStatusIcon;
    }
    private List<Tournament> fetchAllTournaments() {
        List<Tournament> tournamentList = tournamentService.getTournaments();
        if (!tournamentList.isEmpty()) return tournamentList;

        new Notification("Nie znaleziono zawodów").open();
        return tournamentList;
    }


    private void setPastAndCanceledTournamentsVisible(boolean visible) {
        List<Tournament> tournamentList;
        if(!visible) {
             tournamentList = tournamentService.getIncomingAndOngoingTournaments();
             if (tournamentList.isEmpty())  {
                 new Notification("Nie znaleziono zawodów").open();
                 return;
             }
        }
        else tournamentList = fetchAllTournaments();
        grid.setItems(tournamentList);
        grid.getDataProvider().refreshAll();
    }

}
