package com.andrewaleynik.reportdesigner.reportdesigner.models;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quality_id")
    private ElementQuality quality;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", foreignKey = @ForeignKey(name = "fk_unit_id"))
    private PropertyUnit unit;
    private String currentValue;
    private String qualityCriterionValue;


    public Long getId() {
        return this.id;
    }

    public PropertyUnit getUnit() {
        return this.unit;
    }

    public void setUnit(PropertyUnit unit) {
        this.unit = unit;
    }

    public ElementQuality getQuality() {
        return quality;
    }

    public void setQuality(ElementQuality quality) {
        this.quality = quality;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getQualityCriterionValue() {
        return qualityCriterionValue;
    }

    public void setQualityCriterionValue(String qualityCriterionValue) {
        this.qualityCriterionValue = qualityCriterionValue;
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", quality=" + Optional.ofNullable(quality.getCode()).orElse("") +
                ", unit=" + unit +
                ", currentValue='" + currentValue + '\'' +
                ", qualityCriterionValue='" + qualityCriterionValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(id, property.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
