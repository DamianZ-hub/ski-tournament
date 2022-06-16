package com.ski.tournament.views.startlist;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import java.util.HashMap;


public class GenerateStartListPropertiesDialog extends Dialog {

    private FormLayout formLayout;
    private IntegerField firstNr;
    private IntegerField numberOfContenders;
    private CheckboxGroup<String> checkboxGroup;
    private Button closeBtn;

    private HashMap<String,Object> result;


    public GenerateStartListPropertiesDialog() {
        super();
        formLayout = new FormLayout();
        numberOfContenders = new IntegerField();
        numberOfContenders.setLabel("Liczba zawodników");
        numberOfContenders.setHasControls(true);
        numberOfContenders.setMin(1);
        firstNr = new IntegerField();
        firstNr.setLabel("Numer początkowy");
        firstNr.setHasControls(true);
        firstNr.setMin(1);

        checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Generuj dla płci: ");
        checkboxGroup.setItems("Mezczyzna", "Kobieta");

        closeBtn = new Button("Ok");
        closeBtn.addClickListener(buttonClickEvent -> {

            result = new HashMap<>();
            result.put("OK",true);
            result.put("NR",firstNr.getValue());
            result.put("NUMBER_OF_CONTENDERS",numberOfContenders.getValue());
            result.put("GENDER",checkboxGroup.getValue());
            close();
        });

        formLayout.add(numberOfContenders,firstNr,checkboxGroup,closeBtn);
        add(formLayout);

        this.addDialogCloseActionListener(e -> {
            result = new HashMap<>();
            result.put("OK",false);
            close();
        });
    }

    public HashMap<String, Object> getResult() {
        return result;
    }
}
