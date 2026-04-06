package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.StringJoiner;

@NoArgsConstructor
@Entity
@Table(name = "external_influences")
public class ExternalInfluence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "external_influence_group_id")
    private ExternalInfluenceGroup externalInfluenceGroup;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExternalInfluenceGroup getExternalInfluenceGroup() {
        return externalInfluenceGroup;
    }

    public void setExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup) {
        this.externalInfluenceGroup = externalInfluenceGroup;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "ExternalInfluence{", "}")
                .add("id=" + id)
                .add("name=" + name)
                .add("description=" + description)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalInfluence externalInfluence)) return false;
        return id != null && id.equals(externalInfluence.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
