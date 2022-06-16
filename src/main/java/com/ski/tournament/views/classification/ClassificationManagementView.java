package com.ski.tournament.views.classification;

import com.ski.tournament.core.ClassificationType;
import com.ski.tournament.core.ManagementView;
import com.ski.tournament.core.TextFilterField;
import com.ski.tournament.model.Classification;
import com.ski.tournament.model.Tournament;
import com.ski.tournament.service.ClassificationService;
import com.ski.tournament.service.TournamentService;
import com.ski.tournament.views.MainLayout;

import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("Zarządzanie klasyfikacjami")
@Route(value = "classifications-management/:classificationID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_Staff")
public class ClassificationManagementView extends ManagementView<Classification, ClassificationService> {


    private TextField name;
    private MultiComboBox<Integer> tournamentsSelect;
    private Select<ClassificationType> classificationTypeSelect;

    private TournamentService tournamentService;

    private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm";
    List<Tournament> tournamentList;
    private Set<Integer> ids;

    public ClassificationManagementView(@Autowired ClassificationService classificationService,
                                        @Autowired TournamentService tournamentService) {
        super(classificationService, Classification.class, "classification");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        this.tournamentService = tournamentService;
        initialize();
    }

    @Override
    public void initialize() {
        this.setSizeFull();
        this.tournamentList = fetchAllTournaments();
        ids = TournamentsToIds(tournamentList);
        tournamentsSelect.setItems(ids);
    }



    @Override
    public Component[] addFieldsToEditorPanel() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        name = new TextField("Nazwa klasyfikacji");

        classificationTypeSelect = new Select<>();
        classificationTypeSelect.setLabel("Typ tabeli klasyfikacji");
        classificationTypeSelect.setItems(ClassificationType.GENERAL_CLASSIFICATION_RECTOR_CUP,
                ClassificationType.GENERAL_CLASSIFICATION_KU_AZS_CSIR_CUP,
                ClassificationType.GENERAL_TEAM_CLASSIFICATION);
        classificationTypeSelect.setItemLabelGenerator(ClassificationType::getLabel1);
        tournamentsSelect = new MultiComboBox<>();
        tournamentsSelect.setItemLabelGenerator(tournamentId -> {
            Tournament tournament = tournamentList.stream()
                    .filter(tournament1 -> tournament1.getId().equals(tournamentId))
                    .findAny()
                    .orElse(null);
            return tournament == null ? String.valueOf(tournamentId) : tournament.getDateTime().format(formatter) + " - " +
                    tournament.getCompetition().label1 +

                    (tournament.getDescription() == null || tournament.getDescription().isEmpty() ? "" : " - " +
                            tournament.getDescription());
        });
        tournamentsSelect.setLabel("Dostępne zawody");
        Component[] fields = new Component[]{name, classificationTypeSelect, tournamentsSelect};
        return fields;
    }

    @Override
    public void addColumnsToGrid() {

        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        EnhancedColumn<Classification> classificationNameColumn = (EnhancedColumn<Classification>) grid.addColumn("name");
        classificationNameColumn.setHeader("Nazwa");
        EnhancedColumn<Classification> classificationTypeColumn = (EnhancedColumn<Classification>) grid.addColumn("classificationType.label1");
        classificationTypeColumn.setHeader("Typ tabeli klasyfikacji");
        EnhancedColumn<Classification> classificationTournamentsIdsColumn = (EnhancedColumn<Classification>) grid.addColumn("tablesIds");
        classificationTournamentsIdsColumn.setHeader("Zawody");

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    @Override
    protected void configureBinder() {
        binder = new BeanValidationBinder<>(Classification.class);
        binder.forField(name).bind("name");
        binder.forField(classificationTypeSelect).bind("classificationType");
        binder.bind(tournamentsSelect, Classification::getTablesIds,Classification::setTablesIds);
    }

    private List<Tournament> fetchAllTournaments() {
        List<Tournament> tournamentList = tournamentService.getTournaments();
        if (!tournamentList.isEmpty()) return tournamentList;

        new Notification("Nie znaleziono zawodów").open();
        return tournamentList;
    }

    public static Set<Integer> TournamentsToIds(List<Tournament> tournamentList) {

        Set<Integer> ids = new HashSet<>();
        tournamentList.forEach(tournament -> {
            ids.add(tournament.getId());
        });
        return ids;
    }

}
