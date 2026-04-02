package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.StringJoiner;

@NoArgsConstructor
@Entity
@Table(name = "property_values",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"property_id", "external_influence_level_id"},
                name = "uk_property_level"
        ))
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
    @ManyToOne
    @JoinColumn(name = "external_influence_level_id")
    private ExternalInfluenceLevel externalInfluenceLevel;
    private String value;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public ExternalInfluenceLevel getExternalInfluenceLevel() {
        return externalInfluenceLevel;
    }

    public void setExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel) {
        this.externalInfluenceLevel = externalInfluenceLevel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "PropertyValue{", "}")
                .add("id=" + id)
                .add("property=" + property)
                .add("externalInfluenceLevel=" + externalInfluenceLevel)
                .add("value=" + value)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyValue propertyValue)) return false;
        if (id != null && propertyValue.id != null) {
            return id.equals(propertyValue.id);
        }
        return Objects.equals(property, propertyValue.property)
                && Objects.equals(externalInfluenceLevel, propertyValue.externalInfluenceLevel)
                && Objects.equals(value, propertyValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, externalInfluenceLevel, value);
    }
}
