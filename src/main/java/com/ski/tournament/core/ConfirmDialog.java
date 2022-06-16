package com.ski.tournament.core;

import com.vaadin.componentfactory.EnhancedDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;

import java.util.HashMap;

public class ConfirmDialog extends EnhancedDialog {

    private HashMap<String,Object> result;
    private Button yesBtn;
    private Button noBtn;

    public ConfirmDialog() {
        super();
        yesBtn = new Button("Tak");
        noBtn = new Button("Nie");

        yesBtn.addClickListener(buttonClickEvent -> {
            yesBtnListener(buttonClickEvent);
        });
        noBtn.addClickListener(buttonClickEvent -> {
            noBtnListener(buttonClickEvent);
        });

        this.addDialogCloseActionListener(e -> {
            dialogCloseListener(e);
        });

        setHeader("Potwierdzenie");
        setContent(new Span("Czy napewno?"));
        setFooter(yesBtn,noBtn);
    }

    protected void dialogCloseListener(DialogCloseActionEvent e) {
       close();
    }

    protected void yesBtnListener(ClickEvent<Button> buttonClickEvent) {
        result = new HashMap<>();
        result.put("OK",true);
        close();
    }
    protected void noBtnListener(ClickEvent<Button> buttonClickEvent) {
        result = new HashMap<>();
        result.put("OK",false);
        close();
    }
}
