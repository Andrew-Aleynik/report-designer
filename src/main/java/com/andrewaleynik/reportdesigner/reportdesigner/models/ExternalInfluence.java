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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalInfluence externalInfluence)) return false;
        if (id != null && externalInfluence.id != null) {
            return id.equals(externalInfluence.id);
        }
        return Objects.equals(name, externalInfluence.name)
                && Objects.equals(description, externalInfluence.description);
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
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
