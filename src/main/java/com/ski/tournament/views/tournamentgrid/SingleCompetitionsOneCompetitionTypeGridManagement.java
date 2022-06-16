package com.ski.tournament.views.tournamentgrid;

import com.ski.tournament.core.CompetitionType;
import com.ski.tournament.core.ManagementView;
import com.ski.tournament.core.RideStatus;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.model.SingleCompetitionsOneCompetitionTypeData;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.service.SingleCompetitionsOneCompetitionTypeDataService;
import com.ski.tournament.service.UnitService;
import com.ski.tournament.views.tournaments.TournamentTableManagementView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;

import java.util.*;
import java.util.stream.Collectors;

public class SingleCompetitionsOneCompetitionTypeGridManagement extends ManagementView<SingleCompetitionsOneCompetitionTypeData, SingleCompetitionsOneCompetitionTypeDataService> {


    private PersonTournamentDataService personTournamentDataService;
    private Integer tournamentID;
    private CompetitionType competitionType;
    private String baseUrl;

    private List<PersonTournamentData> personTournamentDataList;
    private Select<PersonTournamentData> personTournamentDataSelect;
    private IntegerField takenPlace;
    private NumberField firstRideTime;
    private NumberField secondRideTime;
    private NumberField sumariseRideTime;
    private IntegerField score;
    private Select<RideStatus> rideStatusSelect;

    private Button calculateBtn;

    private SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;

    public SingleCompetitionsOneCompetitionTypeGridManagement(SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService, UnitService unitService,
                                                              PersonTournamentDataService personTournamentDataService, Integer tournamentID, CompetitionType competitionType) {
        super(singleCompetitionsOneCompetitionTypeDataService, SingleCompetitionsOneCompetitionTypeData.class, "tournament-score", new HashMap<>() {{
            put("UnitService", unitService);
            put("PersonTournamentDataService", personTournamentDataService);
            put("CompetitionType", competitionType);
            put("TournamentID", tournamentID);
        }});

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        initialize();
    }


    @Override
    public void initialize() {
        this.setSizeFull();

        personTournamentDataList = fetchPersonTournamentDataForTournamentIdAndNrNotNull();
        personTournamentDataSelect.setItems(fetchPersonTournamentDataForTournamentIdAndNrNotNull());
    }

    @Override
    protected String setUrl(String... args) {
        this.competitionType = (CompetitionType) parameters.get("CompetitionType");
        this.tournamentID = (Integer) parameters.get("TournamentID");

        this.baseUrl = RouteConfiguration.forSessionScope().getUrl(
                TournamentTableManagementView.class,
                new RouteParameters(new HashMap<String, String>() {{
                    put("competition-type", competitionType.label2);
                    put("tournamentID", tournamentID.toString());
                }}));

        return baseUrl + "/%d/edit";

    }

    @Override
    public Component[] addFieldsToEditorPanel() {
        personTournamentDataService = (PersonTournamentDataService) parameters.get("PersonTournamentDataService");

        rideStatusSelect = new Select<>();
        rideStatusSelect.setLabel("Status przejazdu");
        rideStatusSelect.setItems(RideStatus.DS,
                RideStatus.DNS,
                RideStatus.DNF,
                RideStatus.DSQ);
        rideStatusSelect.setItemLabelGenerator(ride -> {
            return ride.toString() + " - " + ride.getLabel1();
        });

        rideStatusSelect.setEnabled(false);
        rideStatusSelect.addValueChangeListener(e -> {
            boolean didStart = e.getValue()!=null ? e.getValue().equals(RideStatus.DS) : false;

            firstRideTime.setEnabled(didStart);
            secondRideTime.setEnabled(didStart);
            calculateBtn.setEnabled(didStart);

        });
        personTournamentDataSelect = new Select<>();
        personTournamentDataSelect.setLabel("Zawodnik");
        personTournamentDataSelect.setItemLabelGenerator(PersonTournamentData::getNrFirstNameLastNameAndUnit);
        personTournamentDataSelect.addValueChangeListener(e -> {
           boolean personSelected = personTournamentDataSelect.getValue() == null ?  false : true;

            rideStatusSelect.setEnabled(personSelected);
        });

        takenPlace = new IntegerField();
        takenPlace.setLabel("Zajęte miejsce");
        takenPlace.setHasControls(true);
        takenPlace.setMin(1);
        takenPlace.setReadOnly(true);


        firstRideTime = new NumberField();
        firstRideTime.setLabel("Czas I przejazdu");
        firstRideTime.setHasControls(true);
        firstRideTime.setMin(0);
        firstRideTime.setStep(0.01d);
        firstRideTime.setEnabled(false);

        secondRideTime = new NumberField();
        secondRideTime.setLabel("Czas II przejazdu");
        secondRideTime.setHasControls(true);
        secondRideTime.setMin(0);
        secondRideTime.setStep(0.01d);
        secondRideTime.setEnabled(false);

        sumariseRideTime = new NumberField();
        sumariseRideTime.setLabel("Sumaryczny czas przejazdów");
        sumariseRideTime.setHasControls(true);
        sumariseRideTime.setMin(0);
        sumariseRideTime.setStep(0.01d);
        sumariseRideTime.setReadOnly(true);

        score = new IntegerField();
        score.setLabel("Punkty");
        score.setHasControls(true);
        score.setMin(0);
        score.setReadOnly(true);

        calculateBtn = new Button("Przelicz i zapisz");
        calculateBtn.setWidthFull();
        calculateBtn.setEnabled(false);

        calculateBtn.addClickListener(e -> {
            if(firstRideTime.getValue() != null &  secondRideTime.getValue() != null) {
                List<SingleCompetitionsOneCompetitionTypeData> dataList = (List) this.grid.getDataProvider().withConfigurableFilter().fetch(new Query()).collect(Collectors.toList());
                List<SingleCompetitionsOneCompetitionTypeData> dataListDidStart = new ArrayList<>();
                dataList.stream().forEach( s -> {
                    if(s.getRideStatus() != null && s.getRideStatus().equals(RideStatus.DS)) dataListDidStart.add(s);
                });
                List<SingleCompetitionsOneCompetitionTypeData> calculatedDataList = service.calculate(dataListDidStart);
                calculatedDataList.forEach(singleCompetitionsOneCompetitionTypeData -> service.update(singleCompetitionsOneCompetitionTypeData));
                refreshGrid();
            }
        });

        Component[] fields = new Component[]{personTournamentDataSelect, rideStatusSelect, firstRideTime, secondRideTime, sumariseRideTime, takenPlace, score, calculateBtn};
        return fields;
    }

    @Override
    public void addColumnsToGrid() {

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        Grid.Column<SingleCompetitionsOneCompetitionTypeData> takenPlaceColumn = grid.addColumn(singleCompetitionsOneCompetitionTypeData -> singleCompetitionsOneCompetitionTypeData.getTakenPlace()).setHeader("Zajęte miejsce").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> singleCompetitionsOneCompetitionTypeData.getPersonTournamentData().getNrFirstNameLastNameAndUnit()).setHeader("Zawodnik").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> singleCompetitionsOneCompetitionTypeData.getFirstRideTime()).setHeader("Czas I przejazdu").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> singleCompetitionsOneCompetitionTypeData.getSecondRideTime()).setHeader("Czas II przejazdu").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> singleCompetitionsOneCompetitionTypeData.getSumariseRideTime()).setHeader("Sumaryczny czas przejazdów").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> singleCompetitionsOneCompetitionTypeData.getScore()).setHeader("Punkty").setAutoWidth(true);
        grid.addColumn(singleCompetitionsOneCompetitionTypeData -> {
            return singleCompetitionsOneCompetitionTypeData.getRideStatus()!=null ? singleCompetitionsOneCompetitionTypeData.getRideStatus().toString() : "";
        }).setHeader("Status przejazdu").setAutoWidth(true);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        List<GridSortOrder<SingleCompetitionsOneCompetitionTypeData>> sortByTakenPlace = new GridSortOrderBuilder<SingleCompetitionsOneCompetitionTypeData>().thenDesc(takenPlaceColumn).build();
        grid.sort(sortByTakenPlace);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);


    }

    @Override
    protected void configureBinder() {
        binder = new BeanValidationBinder<>(SingleCompetitionsOneCompetitionTypeData.class);

        binder.bind(personTournamentDataSelect, "personTournamentData");
        binder.bind(rideStatusSelect,"rideStatus");
        binder.bind(firstRideTime, "firstRideTime");
        binder.bind(secondRideTime, "secondRideTime");
        binder.bind(sumariseRideTime, "sumariseRideTime");
        binder.bind(takenPlace, "takenPlace");
        binder.bind(score, "score");

    }

    @Override
    protected void save() {
        try {
            if (this.entity == null) {
                this.entity = new SingleCompetitionsOneCompetitionTypeData();
            }
            binder.writeBean(this.entity);

            service.update(this.entity);
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(baseUrl);
            grid.getDataProvider().refreshAll();
            setDataProvider();
            Notification.show("Rekord został zapisany");
        } catch (ValidationException validationException) {
            Notification.show("Wystąpił błąd podczas próby zapisania rekordu.");
        }
    }

    @Override
    protected void delete() {
        if (this.entity != null) {
            service.delete(this.entity.getId());
            clearForm();
            refreshGrid();
            Notification.show("Rekord został usunięty");
            UI.getCurrent().navigate(baseUrl);
            grid.getDataProvider().refreshAll();
            setDataProvider();

        } else Notification.show("Nie wybrano żadnego rekordu");
    }

    private List<PersonTournamentData> fetchPersonTournamentDataForTournamentIdAndNrNotNull() {
        List<PersonTournamentData> personTournamentDataList = personTournamentDataService.findAllByTournamentIdAndNrNotNull(tournamentID);
        Comparator<PersonTournamentData> compareByNr = (PersonTournamentData o1, PersonTournamentData o2) ->
                o1.getNr().compareTo( o2.getNr() );
        Collections.sort(personTournamentDataList,compareByNr);
        if (!personTournamentDataList.isEmpty()) return personTournamentDataList;

        new Notification("Nie znaleziono zawodników").open();
        return personTournamentDataList;
    }

    @Override
    protected void valueChanged(AbstractField.ComponentValueChangeEvent<Grid<SingleCompetitionsOneCompetitionTypeData>, SingleCompetitionsOneCompetitionTypeData> event) {
        if (event.getValue() != null) {
            UI.getCurrent().navigate(String.format(ENTITY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
        } else {
            clearForm();
            UI.getCurrent().
                    navigate(baseUrl);
        }
    }

    @Override
    protected void setDataProvider() {
        grid.setDataProvider(DataProvider.ofCollection(service.getAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(tournamentID)));
    }
}

