package com.andrewaleynik.reportdesigner.reportdesigner.models;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Objects;

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
        return "PropertyUnit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyUnit that = (PropertyUnit) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
