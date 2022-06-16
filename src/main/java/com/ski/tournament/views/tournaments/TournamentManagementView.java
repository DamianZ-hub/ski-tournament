package com.ski.tournament.views.tournaments;

import com.ski.tournament.core.*;
import com.ski.tournament.model.Place;
import com.ski.tournament.model.Tournament;
import com.ski.tournament.model.Unit;
import com.ski.tournament.service.*;
import com.ski.tournament.views.MainLayout;
import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.HashMap;
import java.util.List;

@PageTitle("Zarządzanie zawodami")
@Route(value = "tournaments-management/:tournamentID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_Staff")
public class TournamentManagementView extends ManagementView<Tournament, TournamentService> {


    private UnitService unitService;
    private PlaceService placeService;
    private SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService;
    private SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService;
    private SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService;

    private DateTimePicker dateTimePicker;
    private Select<TournamentStatus> statusSelect;
    private Select<Unit> unitSelect;
    private Select<Place> placeSelect;
    private Select<Competition> competitionSelect;
    private Select<CompetitionType> competitionTypeSelect;
    private TextField description;

    private CompetitionType competitionSelectOldValue;
    private Button tournamentScoreTablebtn;


    public TournamentManagementView(@Autowired TournamentService tournamentService, @Autowired  UnitService unitService, @Autowired PlaceService placeService,
                                    @Autowired SingleCompetitionsOneCompetitionTypeDataService singleCompetitionsOneCompetitionTypeDataService,
                                    @Autowired SingleCompetitionsUnitMasteryDataService singleCompetitionsUnitMasteryDataService,
                                    @Autowired SingleCompetitionsSponsorCupDataService singleCompetitionsSponsorCupDataService){
        super(tournamentService, Tournament.class,"tournament");

        this.unitService = unitService;
        this.placeService = placeService;
        this.singleCompetitionsSponsorCupDataService = singleCompetitionsSponsorCupDataService;
        this.singleCompetitionsOneCompetitionTypeDataService = singleCompetitionsOneCompetitionTypeDataService;
        this.singleCompetitionsUnitMasteryDataService = singleCompetitionsUnitMasteryDataService;

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        initialize();
    }
    @Override
    public void initialize() {
        this.setSizeFull();
        unitSelect.setItems(fetchAllUnits());
        placeSelect.setItems(fetchAllPlaces());
    }

    @Override
    public Component[] addFieldsToEditorPanel() {

        description = new TextField("Opis");


        dateTimePicker = new DateTimePicker("Data zawodów");
        dateTimePicker.setDatePlaceholder("Data");
        dateTimePicker.setTimePlaceholder("Czas rozpoczęcia");

        statusSelect = new Select<>();
        statusSelect.setItems(TournamentStatus.INCOMING,TournamentStatus.CANCELED,TournamentStatus.PAST,TournamentStatus.ONGOING);
        statusSelect.setLabel("Status");
        statusSelect.setItemLabelGenerator(TournamentStatus::getLabel);

        unitSelect = new Select<>();
        unitSelect.setLabel("Jednostka organizacyjna");
        unitSelect.setItemLabelGenerator(Unit::getFullName);

        placeSelect = new Select<>();
        placeSelect.setLabel("Miejsce");
        placeSelect.setItemLabelGenerator(Place::getTitle);

        competitionSelect = new Select<>();
        competitionSelect.setLabel("Konkurencja");
        competitionSelect.setItems(Competition.GIGANT,Competition.SLALOM);
        competitionSelect.setItemLabelGenerator(Competition::getLabel1);

        competitionTypeSelect = new Select<>();
        competitionTypeSelect.setLabel("Typ tabeli");
        competitionTypeSelect.setItems(CompetitionType.SINGLE_COMPETITIONS_SPONSOR_CUP,
                CompetitionType.SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE,
                CompetitionType.SINGLE_COMPETITIONS_UNIT_MASTERY);
        competitionTypeSelect.setItemLabelGenerator(CompetitionType::getLabel1);

        tournamentScoreTablebtn = new Button("Tabela");
        tournamentScoreTablebtn.addClickListener(e ->
                tournamentScoreTablebtn.getUI().ifPresent(ui -> {
                            if (this.entity != null) ui.navigate(
                                    TournamentTableManagementView.class,
                                    new RouteParameters(new HashMap<String, String>() {{
                                        put("competition-type",entity.getCompetitionType().label2);
                                        put("tournamentID", entity.getId().toString());
                                    }}));
                        }
                )
        );
        competitionTypeSelect.addValueChangeListener(e -> {
            setCompetitionSelectValueChangeListener(e);
        });
        Component[] fields = new Component[]{statusSelect, dateTimePicker, placeSelect, unitSelect, competitionSelect,competitionTypeSelect, description, tournamentScoreTablebtn};
        return fields;
    }

    @Override
    protected void save() {
        if(!(competitionSelectOldValue == null || competitionSelectOldValue.toString().isEmpty() || entity.getId() == null)) {
            if(checkIfRecordsExists(competitionSelectOldValue)) {
                ConfirmDialog deleteExistingRecordsDialog = new ConfirmDialog() {
                    @Override
                    protected void noBtnListener(ClickEvent<Button> buttonClickEvent) {
                        competitionTypeSelect.setValue(competitionSelectOldValue);
                        this.close();
                        return;
                    }

                    @Override
                    protected void yesBtnListener(ClickEvent<Button> buttonClickEvent) {
                        deleteRecordsByCompetitionTypeAndTournamentId(competitionSelectOldValue);
                        TournamentManagementView.super.save();
                        this.close();
                        return;
                    }
                };
                deleteExistingRecordsDialog.setHeader("Ostrzeżenie!");
                deleteExistingRecordsDialog.setContent(new Span("Próbujesz zmienić typ tabeli w której znajdują się już rekordy.<br>Jeżeli to zrobisz zostaną one usunięte.<br>Czy chcesz kontynuować?"));
                deleteExistingRecordsDialog.open();
            }
            else super.save();
        }
       else super.save();
    }

    private void setCompetitionSelectValueChangeListener(AbstractField.ComponentValueChangeEvent<Select<CompetitionType>, CompetitionType> e) {
        competitionSelectOldValue = e.getOldValue();
    }

    private boolean checkIfRecordsExists(CompetitionType competitionSelectValue) {
        boolean recordsExists;
        switch(competitionSelectValue) {
            case SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE:
              recordsExists = singleCompetitionsOneCompetitionTypeDataService.checkIfExistsSingleCompetitionsOneCompetitionTypeDataByTournamentId(entity.getId());
                break;
            case SINGLE_COMPETITIONS_SPONSOR_CUP:
                recordsExists = singleCompetitionsSponsorCupDataService.checkIfExistsSingleCompetitionsSponsorCupDataByTournamentId(entity.getId());
                break;
            case SINGLE_COMPETITIONS_UNIT_MASTERY:
                recordsExists = singleCompetitionsUnitMasteryDataService.checkIfExistsSingleCompetitionsUnitMasteryDataByTournamentId(entity.getId());
                break;
            default:
                new Notification("Nieprawidłowy typ tabeli").open();
                recordsExists=false;
                break;
        }
        return recordsExists;
    }

    private void deleteRecordsByCompetitionTypeAndTournamentId(CompetitionType competitionSelectValue) {
        switch(competitionSelectValue) {
            case SINGLE_COMPETITIONS_ONE_COMPETITION_TYPE:
                singleCompetitionsOneCompetitionTypeDataService.deleteAllSingleCompetitionsOneCompetitionTypeDataByTournamentId(entity.getId());
                break;
            case SINGLE_COMPETITIONS_SPONSOR_CUP:
                singleCompetitionsSponsorCupDataService.deleteAllSingleCompetitionsSponsorCupDataByTournamentId(entity.getId());
                break;
            case SINGLE_COMPETITIONS_UNIT_MASTERY:
                singleCompetitionsUnitMasteryDataService.deleteAllSingleCompetitionsUnitMasteryDataByTournamentId(entity.getId());
                break;
            default:
                new Notification("Nieprawidłowy typ tabeli").open();
                break;
        }
    }

    @Override
    public void addColumnsToGrid() {

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<Tournament> statusColumn = (EnhancedColumn<Tournament>) grid.addColumn("status.label");
        statusColumn.setHeader("Status");
        EnhancedColumn<Tournament> dateColumn = (EnhancedColumn<Tournament>) grid.addColumn("dateTime");
        dateColumn.setHeader("Data");
        EnhancedColumn<Tournament> placeColumn = (EnhancedColumn<Tournament>) grid.addColumn("place.title");
        placeColumn.setHeader("Miejsce");
        EnhancedColumn<Tournament> competitionColumn = (EnhancedColumn<Tournament>) grid.addColumn("competition.label1");
        competitionColumn.setHeader("Konkurencja");
        EnhancedColumn<Tournament> competitionTypeColumn = (EnhancedColumn<Tournament>) grid.addColumn("competitionType.label1");
        competitionTypeColumn.setHeader("Typ tabeli");
        EnhancedColumn<Tournament> unitColumn = (EnhancedColumn<Tournament>) grid.addColumn("unit.fullName");
        unitColumn.setHeader("Jednostka organizacyjny");
        EnhancedColumn<Tournament> descriptionColumn = (EnhancedColumn<Tournament>) grid.addColumn("description");
        competitionTypeColumn.setHeader("Opis");
        List<GridSortOrder<Tournament>> sortByDate = new GridSortOrderBuilder<Tournament>().thenAsc(dateColumn).build();
        grid.sort(sortByDate);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

    }

    @Override
    protected void configureBinder() {
        binder = new BeanValidationBinder<>(Tournament.class);
        binder.forField(dateTimePicker).bind("dateTime");
        binder.forField(statusSelect).bind("status");
        binder.bind(unitSelect,"unit");
        binder.forField(placeSelect).bind("place");
        binder.forField(competitionSelect).bind("competition");
        binder.forField(competitionTypeSelect).bind("competitionType");
        binder.forField(description).bind("description");



    }

    private List<Unit> fetchAllUnits() {
        List<Unit> unitsList= unitService.getUnits();
        if(!unitsList.isEmpty()) return unitsList;

            new Notification("Nie znaleziono jednostek organizacyjnych").open();
            return unitsList;
    }

    private List<Place> fetchAllPlaces() {
        List<Place> placeList= placeService.getPlaces();
        if(!placeList.isEmpty()) return placeList;

        new Notification("Nie znaleziono miejsc").open();
        return placeList;
    }
}
