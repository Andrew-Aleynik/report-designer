package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "quality_property",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "quality_id")
    )
    private Set<ElementQuality> qualities = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", foreignKey = @ForeignKey(name = "fk_unit_id"))
    private PropertyUnit unit;
    private String currentValue;


    public Long getId() {
        return this.id;
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


    public String getCurrentValue() {
        return currentValue;
    }

    public void setQualityCriterionValue(String qualityCriterionValue) {
        this.currentValue = currentValue;
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
                .add("currentValue=" + currentValue)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property property)) return false;
        if (id != null && property.id != null) {
            return id.equals(property.id);
        }
        return Objects.equals(qualities, property.qualities)
                && Objects.equals(unit, property.unit)
                && Objects.equals(currentValue, property.currentValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualities, unit, currentValue);
    }
}
