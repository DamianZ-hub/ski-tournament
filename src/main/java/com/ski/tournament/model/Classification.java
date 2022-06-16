package com.ski.tournament.model;

import com.ski.tournament.core.ClassificationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class Classification extends AbstractEntity{

    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ClassificationType classificationType;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> tablesIds;

    public Classification(@NotNull String name,@NotNull ClassificationType classificationType, Set<Integer> tablesIds) {
        this.name = name;
        this.classificationType = classificationType;
        this.tablesIds = tablesIds;
    }

    public Classification() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassificationType getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(ClassificationType classificationType) {
        this.classificationType = classificationType;
    }

    public Set<Integer> getTablesIds() {
        return tablesIds;
    }

    public void setTablesIds(Set<Integer> tablesIds) {
        this.tablesIds = tablesIds;
    }
}
