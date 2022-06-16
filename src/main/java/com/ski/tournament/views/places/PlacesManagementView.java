package com.ski.tournament.views.places;


import com.ski.tournament.core.ManagementView;
import com.ski.tournament.model.Place;
import com.ski.tournament.service.PlaceService;
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

@PageTitle("Zarządzanie miejscami")
@Route(value = "places-management/:placeID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Secured("ROLE_Staff")
public class PlacesManagementView extends ManagementView<Place,PlaceService> {

    private TextField title;
    private TextField subtitle;
    private TextField description;
    private TextField altText;
    private TextField imageUrl;


    public PlacesManagementView(@Autowired PlaceService placeService) {
        super(placeService,Place.class,"place");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }
    @Override
    public Component[] addFieldsToEditorPanel() {
        title = new TextField("Nazwa");
        subtitle = new TextField("Podtytuł");
        description = new TextField("Opis");
        altText = new TextField("Alt tekst");
        imageUrl = new TextField("Url");

        Component[] fields = new Component[]{title,subtitle,description,altText,imageUrl};
        return fields;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void addColumnsToGrid() {

        grid.addColumn("title").setHeader("Nazwa").setAutoWidth(true);
        grid.addColumn("subtitle").setHeader("Podtytuł").setAutoWidth(true);
        grid.addColumn("description").setHeader("Opis").setAutoWidth(true);
        grid.addColumn("altText").setHeader("Alt tekst").setAutoWidth(true);
        grid.addColumn("imageUrl").setHeader("Url").setAutoWidth(true);

    }


}
