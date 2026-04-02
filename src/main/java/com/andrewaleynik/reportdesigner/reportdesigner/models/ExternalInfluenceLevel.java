package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.StringJoiner;

@NoArgsConstructor
@Entity
@Table(name = "external_influence_levels")
public class ExternalInfluenceLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "external_influence_id")
    private ExternalInfluence externalInfluence;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setExternalInfluence(ExternalInfluence externalInfluence) {
        this.externalInfluence = externalInfluence;
    }

    public ExternalInfluence getExternalInfluence() {
        return externalInfluence;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "ExternalInfluenceLevel{", "}")
                .add("id=" + id)
                .add("name=" + name)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalInfluenceLevel level)) return false;

        if (id != null && level.id != null) {
            return id.equals(level.id);
        }

        return Objects.equals(name, level.name)
                && Objects.equals(externalInfluence, level.externalInfluence);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
