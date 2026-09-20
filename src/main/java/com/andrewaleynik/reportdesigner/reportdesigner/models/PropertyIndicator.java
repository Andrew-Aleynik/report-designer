package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@NoArgsConstructor
@Entity
@Table(name = "property_indicators")
public class PropertyIndicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "PropertyIndicator{", "}")
                .add("id=" + id)
                .add("name=" + name)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyIndicator indicator)) return false;
        return id != null && id.equals(indicator.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
