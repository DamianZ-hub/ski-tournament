package com.ski.tournament.views.units;

import com.ski.tournament.core.ManagementView;
import com.ski.tournament.model.Unit;
import com.ski.tournament.service.UnitService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@PageTitle("Zarządzanie jednostkami")
@Route(value = "units-management/:unitID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_Staff")
public class UnitsManagementView extends ManagementView<Unit, UnitService> {

    private TextField shortName;
    private TextField fullName;


    public UnitsManagementView(@Autowired UnitService unitService) {
        super(unitService,Unit.class,"unit");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }
    @Override
    public Component[] addFieldsToEditorPanel() {
        shortName = new TextField("Skrót");
        fullName = new TextField("Nazwa");

        Component[] fields = new Component[]{shortName,fullName};
        return fields;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void addColumnsToGrid() {

        grid.addColumn("shortName").setHeader("Skrót").setAutoWidth(true);
        grid.addColumn("fullName").setHeader("Nazwa").setAutoWidth(true);

    }
}