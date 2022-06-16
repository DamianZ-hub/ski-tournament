package com.ski.tournament.model;


import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "places")
public class Place extends AbstractEntity {

    @Lob
    private String imageUrl;

    private String subtitle;

    private String altText;

    private String description;

    @NotNull
    private String title;


    public Place(@NotNull String title, String imageUrl, String description, String subtitle, String altText) {
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.description = description;
        this.title = title;
        this.subtitle = subtitle;
    }

    public Place(@NotNull String title) {
        this.title = title;
    }

    public Place() {
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
