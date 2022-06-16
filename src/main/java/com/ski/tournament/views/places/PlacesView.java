package com.ski.tournament.views.places;

import com.ski.tournament.model.Place;
import com.ski.tournament.service.PlaceService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@PageTitle("Miejsca")
@Route(value = "places", layout = MainLayout.class)
@Tag("places-view")
@JsModule("./views/places/places-view.ts")
@Secured("ROLE_User")
public class PlacesView extends LitTemplate implements HasComponents, HasStyle {

    private final PlaceService placeService;

    @Autowired
    public PlacesView(PlaceService placeService) {
        this.placeService=placeService;
        addClassNames("places-view", "flex", "flex-col", "h-full");
        fetchAll();
    }

    private void fetchAll(){
        for(Place place : placeService.getPlaces()){
            add(new ImageCard(place.getTitle(),
                    place.getImageUrl(),
                    place.getDescription(),
                    place.getSubtitle(),
                    place.getAltText()));
        }
    }
}