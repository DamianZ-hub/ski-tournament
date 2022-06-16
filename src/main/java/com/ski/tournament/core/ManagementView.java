package com.ski.tournament.core;

import com.ski.tournament.model.AbstractEntity;
import com.ski.tournament.service.PersonService;

import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.vaadin.artur.helpers.CrudService;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public abstract class ManagementView<T extends AbstractEntity,W extends CrudService<T,Integer>> extends Div implements BeforeEnterObserver {

    protected final String ENTITY_ID;

    protected String ENTITY_EDIT_ROUTE_TEMPLATE;

    protected EnhancedGrid<T> grid;




    protected Button cancel = new Button("Anuluj");
    protected Button save = new Button("Zapisz");
    protected Button delete = new Button("Usuń");

    protected BeanValidationBinder<T> binder;

    protected T entity;

    protected W service;

    protected  HashMap<String,Object> parameters;

    private Class<T> entityType;

    public ManagementView(W service, Class<T> entityType, String singular, HashMap<String,Object> parameters) {
        this.service = service;
        this.parameters = parameters;
        this.entityType = entityType;
        this.ENTITY_ID = singular + "ID";
        this.ENTITY_EDIT_ROUTE_TEMPLATE = setUrl(singular,"s-management/%d/edit");
        addClassNames("collaborative-master-detail-view", "flex", "flex-col", "h-full");

        grid = new EnhancedGrid<T>(entityType, false);

        String name = PersonService.getCurrentLoggedUseFirstNameAndLastName();


        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();


        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);


        addColumnsToGrid();

        setDataProvider();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.asSingleSelect().addValueChangeListener(this::valueChanged);

        configureBinder();

        cancel.addClickListener(e -> cancel());

        save.addClickListener(e -> save());

        delete.addClickListener(e -> delete());
    }

    protected void setDataProvider() {
        grid.setDataProvider(new CrudServiceDataProvider<>(service));
    }


    public ManagementView(W service, Class<T> entityType, String singular) {
       this(service, entityType, singular,new HashMap<>());
    }


    protected void save(){
        try {
            if (this.entity == null) {
                this.entity = createInstance();
            }
            binder.writeBean(this.entity);

            service.update(this.entity);
            clearForm();
            refreshGrid();
            Notification.show("Rekord został zapisany");
            UI.getCurrent().navigate(this.getClass());
        } catch (ValidationException validationException) {
            Notification.show("Wystąpił błąd podczas próby zapisania rekordu.");
            validationException.printStackTrace();
        } catch (InvocationTargetException e) {
            Notification.show("Wystąpił błąd podczas próby zapisania rekordu.");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Notification.show("Wystąpił błąd podczas próby zapisania rekordu.");
            e.printStackTrace();
        } catch (InstantiationException e) {
            Notification.show("Wystąpił błąd podczas próby zapisania rekordu.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Notification.show("Wystąpił błąd podczas próby zapisania rekordu.");
            e.printStackTrace();
        }
        catch (Exception e) {
            Dialog dialog = new Dialog();
            dialog.add(e.getMessage());
            dialog.open();
        }
    }

    private T createInstance() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return entityType.getDeclaredConstructor().newInstance();
    }
    protected String setUrl(String... args){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<args.length;i++){
            sb.append(args[i]);
        }
        return sb.toString();
    }
    protected void delete() {
        if (this.entity != null) {
            try{
                service.delete(this.entity.getId());
                clearForm();
                refreshGrid();
                Notification.show("Rekord został usunięty");
                UI.getCurrent().navigate(this.getClass());
            } catch (Exception e) {
                Dialog dialog = new Dialog();
                dialog.add(e.getMessage());
                dialog.open();
            }

        }
        else Notification.show("Nie wybrano żadnego rekordu");
    }

    protected void cancel() {
        clearForm();
        refreshGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> entityId = event.getRouteParameters().getInteger(ENTITY_ID);
        if (entityId.isPresent()) {
            Optional<T> entityFromBackend = service.get(entityId.get());
            if (entityFromBackend.isPresent()) {
                populateForm(entityFromBackend.get());
            } else {
                Notification.show(
                        String.format("Żaden rekord nie został znaleziony, ID = %d", entityId.get()), 3000,
                        Notification.Position.BOTTOM_START);

                refreshGrid();
                event.forwardTo(this.getClass());
            }
        }
    }

    protected void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        Component[] fields = addFieldsToEditorPanel();

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    public abstract Component[] addFieldsToEditorPanel();

    public abstract void initialize();

    public abstract void addColumnsToGrid();

    protected void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.getStyle().set("background-color","red");
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    protected void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    protected void clearForm() {
        populateForm(null);
    }

    protected void populateForm(T value) {
        this.entity = value;
        binder.readBean(this.entity);
    }

    protected void configureBinder(){
        binder = new BeanValidationBinder<>(entityType);

        binder.bindInstanceFields(this);
    }

    protected void valueChanged(AbstractField.ComponentValueChangeEvent<Grid<T>, T> event) {
        if (event.getValue() != null) {
            UI.getCurrent().navigate(String.format(ENTITY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
        } else {
            clearForm();
            UI.getCurrent().
                    navigate(this.getClass());
        }
    }
}
