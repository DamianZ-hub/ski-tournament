package com.ski.tournament.views.classification;

import com.ski.tournament.model.Classification;
import com.ski.tournament.service.ClassificationService;
import com.ski.tournament.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.HashMap;
import java.util.List;

@PageTitle("Klasyfikacje")
@Route(value = "classifications", layout = MainLayout.class)
@Secured("ROLE_User")
public class ClassificationsView extends Div implements AfterNavigationObserver {


    Grid<Classification> grid = new Grid<>();

    private final ClassificationService classificationService;

    @Autowired
    public ClassificationsView(ClassificationService classificationService) {
        this.classificationService = classificationService;
        addClassName("tournaments-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(person -> createCard(person));

        add(grid);
    }

    private HorizontalLayout createCard(Classification classification) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        HorizontalLayout horizontalLayoutContent = new HorizontalLayout();
        horizontalLayoutContent.setAlignItems(FlexComponent.Alignment.CENTER);

        Span competition = new Span(classification.getName());
        competition.addClassName("description");

        horizontalLayoutContent.addAndExpand(competition);

        HorizontalLayout horizontalLayoutBtn = new HorizontalLayout();
        Button table = new Button("tabela");
        table.addClickListener(e ->
                table.getUI().ifPresent(ui ->
                        ui.navigate(
                                ClassificationsTableView.class,
                                new RouteParameters( new HashMap<String,String>(){{
                                    put("classification-type",classification.getClassificationType().label2);
                                    put("classificationID",classification.getId().toString()) ;
                                   }})))
                        );
        horizontalLayoutBtn.add(table);
        horizontalLayoutBtn.setAlignItems(FlexComponent.Alignment.END);

        card.addAndExpand(horizontalLayoutContent,horizontalLayoutBtn);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        List<Classification> classifications = classificationService.getClassifications();
        grid.setItems(classifications);

    }
}
