package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "properties")
    private Set<ElementQuality> qualities = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", foreignKey = @ForeignKey(name = "fk_unit_id"))
    private PropertyUnit unit;
    private String qualityCriterionValue;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropertyUnit getUnit() {
        return this.unit;
    }

    public void setUnit(PropertyUnit unit) {
        this.unit = unit;
    }

    public Set<ElementQuality> getQualities() {
        return qualities;
    }

    void internalAddQuality(ElementQuality quality) {
        this.qualities.add(quality);
    }

    void internalRemoveQuality(ElementQuality quality) {
        this.qualities.remove(quality);
    }

    public void addQuality(ElementQuality quality) {
        if (!qualities.contains(quality)) {
            qualities.add(quality);
            quality.internalAddProperty(this);
        }
    }

    public void removeQuality(ElementQuality quality) {
        if (qualities.contains(quality)) {
            qualities.remove(quality);
            quality.internalRemoveProperty(this);
        }
    }

    public void setQualities(Set<ElementQuality> newQualities) {
        Set<ElementQuality> toRemove = new HashSet<>(this.qualities);
        toRemove.removeAll(newQualities);
        toRemove.forEach(this::removeQuality);

        Set<ElementQuality> toAdd = new HashSet<>(newQualities);
        toAdd.removeAll(this.qualities);
        toAdd.forEach(this::addQuality);
    }


    public String getQualityCriterionValue() {
        return qualityCriterionValue;
    }

    public void setQualityCriterionValue(String qualityCriterionValue) {
        this.qualityCriterionValue = qualityCriterionValue;
    }

    @Override
    public String toString() {
        String qualitiesString = qualities.stream()
                .map(quality -> Optional.ofNullable(quality.getCode()).orElse(""))
                .collect(Collectors.joining(",", "(", ")"));
        return new StringJoiner(", ", "Property{", "}")
                .add("id=" + id)
                .add("qualities=" + qualitiesString)
                .add("unit=" + unit)
                .add("qualityCriterionValue=" + qualityCriterionValue)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property property)) return false;
        return id != null && id.equals(property.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
