package com.ski.tournament.views.tournamentgrid;

import com.ski.tournament.core.CompetitionType;
import com.ski.tournament.core.ManagementView;
import com.ski.tournament.core.RideStatus;
import com.ski.tournament.model.PersonTournamentData;
import com.ski.tournament.model.SingleCompetitionsUnitMasteryData;
import com.ski.tournament.service.PersonTournamentDataService;
import com.ski.tournament.service.SingleCompetitionsUnitMasteryDataService;
import com.ski.tournament.service.UnitService;
import com.ski.tournament.views.tournaments.TournamentTableManagementView;
import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
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

public class SingleCompetitionsUnitMasteryGridManagement extends ManagementView<SingleCompetitionsUnitMasteryData, SingleCompetitionsUnitMasteryDataService>  {


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
    private Select<RideStatus> rideStatusSelect;

    private Button calculateBtn;


    private SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;

    public SingleCompetitionsUnitMasteryGridManagement(SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService, UnitService unitService,
                                                       PersonTournamentDataService personTournamentDataService, Integer tournamentID, CompetitionType competitionType) {
        super(singleCompetitionsUnitMasteryDataService, SingleCompetitionsUnitMasteryData.class, "tournament-score", new HashMap<>()
        {{
          put("UnitService",unitService);
          put("PersonTournamentDataService",personTournamentDataService);
          put("CompetitionType",competitionType);
          put("TournamentID",tournamentID);
        }});

        this.personTournamentDataService = personTournamentDataService;
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
        this.competitionType =  (CompetitionType) parameters.get("CompetitionType");
        this.tournamentID = (Integer) parameters.get("TournamentID");

        this.baseUrl = RouteConfiguration.forSessionScope().getUrl(
                TournamentTableManagementView.class,
                new RouteParameters(new HashMap<String, String>() {{
                    put("competition-type",competitionType.label2);
                    put("tournamentID", tournamentID.toString());
                }}));

        return baseUrl + "/%d/edit";

    }

    @Override
    public Component[] addFieldsToEditorPanel() {

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

        calculateBtn = new Button("Przelicz i zapisz");
        calculateBtn.setWidthFull();
        calculateBtn.setEnabled(false);

        calculateBtn.addClickListener(e -> {
            List<SingleCompetitionsUnitMasteryData> dataList = (List) this.grid.getDataProvider().withConfigurableFilter().fetch(new Query()).collect(Collectors.toList());
            List<SingleCompetitionsUnitMasteryData> dataListDidStart = new ArrayList<>();
            dataList.stream().forEach( s -> {
                if(s.getRideStatus() != null && s.getRideStatus().equals(RideStatus.DS)) dataListDidStart.add(s);
            });
            List<SingleCompetitionsUnitMasteryData> calculatedDataList = service.calculate(dataListDidStart);
            calculatedDataList.forEach(singleCompetitionsUnitMasteryData -> service.update(singleCompetitionsUnitMasteryData));
            refreshGrid();
        });
        Component[] fields = new Component[]{personTournamentDataSelect, rideStatusSelect, firstRideTime, secondRideTime, sumariseRideTime, takenPlace, calculateBtn};
        return fields;
    }


    @Override
    public void addColumnsToGrid() {

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<SingleCompetitionsUnitMasteryData> enhancedColumn = (EnhancedColumn<SingleCompetitionsUnitMasteryData>) grid.addColumn(singleCompetitionsUnitMasteryData -> singleCompetitionsUnitMasteryData.getTakenPlace()).setHeader("Zajęte miejsce").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> singleCompetitionsUnitMasteryData.getPersonTournamentData().getNrFirstNameLastNameAndUnit()).setHeader("Zawodnik").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> singleCompetitionsUnitMasteryData.getFirstRideTime()).setHeader("Czas I przejazdu").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> singleCompetitionsUnitMasteryData.getSecondRideTime()).setHeader("Czas II przejazdu").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> singleCompetitionsUnitMasteryData.getSumariseRideTime()).setHeader("Sumaryczny czas przejazdów").setAutoWidth(true);
        grid.addColumn(singleCompetitionsUnitMasteryData -> {
            return singleCompetitionsUnitMasteryData.getRideStatus()!=null ? singleCompetitionsUnitMasteryData.getRideStatus().toString() : "";
        }).setHeader("Status przejazdu").setAutoWidth(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        List<GridSortOrder<SingleCompetitionsUnitMasteryData>> sortByTakenPlace = new GridSortOrderBuilder<SingleCompetitionsUnitMasteryData>().thenDesc(enhancedColumn).build();
        grid.sort(sortByTakenPlace);

    }

    @Override
    protected void configureBinder() {
        binder = new BeanValidationBinder<>(SingleCompetitionsUnitMasteryData.class);


        binder.bind(personTournamentDataSelect,"personTournamentData");
        binder.bind(rideStatusSelect,"rideStatus");
        binder.bind(firstRideTime,"firstRideTime");
        binder.bind(secondRideTime,"secondRideTime");
        binder.bind(sumariseRideTime,"sumariseRideTime");
        binder.bind(takenPlace,"takenPlace");

    }

    @Override
    protected void save() {
        try {
            if (this.entity == null) {
                this.entity = new SingleCompetitionsUnitMasteryData();
            }
            binder.writeBean(this.entity);

            service.update(this.entity);
            clearForm();
            refreshGrid();
            UI.getCurrent().navigate(baseUrl);
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
            setDataProvider();

        }
        else Notification.show("Nie wybrano żadnego rekordu");
    }

    private List<PersonTournamentData> fetchPersonTournamentDataForTournamentIdAndNrNotNull() {
        List<PersonTournamentData> personTournamentDataList= personTournamentDataService.findAllByTournamentIdAndNrNotNull(tournamentID);
        Comparator<PersonTournamentData> compareByNr = (PersonTournamentData o1, PersonTournamentData o2) ->
                o1.getNr().compareTo( o2.getNr() );
        Collections.sort(personTournamentDataList,compareByNr);
        if(!personTournamentDataList.isEmpty()) return personTournamentDataList;

        new Notification("Nie znaleziono zawodników").open();
        return personTournamentDataList;
    }

    @Override
    protected void valueChanged(AbstractField.ComponentValueChangeEvent<Grid<SingleCompetitionsUnitMasteryData>, SingleCompetitionsUnitMasteryData> event) {
        if (event.getValue() != null) {
            UI.getCurrent().navigate(String.format(ENTITY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
        } else {
            clearForm();
            UI.getCurrent().
                    navigate(baseUrl);
        }
    }

//    @Override
//    protected void setDataProvider() {
//        grid.setDataProvider(new CrudServiceDataProvider<>(service){
//            @Override
//            protected Page<SingleCompetitionsUnitMasteryData> fetchFromBackEnd(Query<SingleCompetitionsUnitMasteryData, Object> query, Pageable pageable) {
//                return service.getAllSingleCompetitionsUnitMasteryDataByTournamentIdPageable(pageable,tournamentID);
//            }
//        });
//    }

    @Override
    protected void setDataProvider() {
        grid.setDataProvider(DataProvider.ofCollection(service.getAllSingleCompetitionsUnitMasteryDataByTournamentId(tournamentID)));
    }
}

