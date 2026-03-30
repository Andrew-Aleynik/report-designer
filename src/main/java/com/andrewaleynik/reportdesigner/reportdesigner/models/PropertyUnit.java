package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.StringJoiner;

@NoArgsConstructor
@Entity
@Table(name = "units")
public class PropertyUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "PropertyUnit{", "}")
                .add("id=" + id)
                .add("name=" + name)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyUnit propertyUnit)) return false;
        if (id != null && propertyUnit.id != null) {
            return id.equals(propertyUnit.id);
        }
        return Objects.equals(name, propertyUnit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
