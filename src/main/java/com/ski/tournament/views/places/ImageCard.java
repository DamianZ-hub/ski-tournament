package com.ski.tournament.views.places;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

@JsModule("./views/places/image-card.ts")
@Tag("image-card")
public class ImageCard extends LitTemplate {

    @Id
    private Image image;

    @Id
    private Span header;

    @Id
    private Span subtitle;

    @Id
    private Paragraph text;

    public ImageCard(String title, String url, String description, String subtitle, String altText ) {
        this.image.setSrc(url);
        this.image.setAlt(altText);
        this.header.setText(title);
        this.subtitle.setText(subtitle);
        this.text.setText(description);
    }
}
