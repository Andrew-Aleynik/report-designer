package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@NoArgsConstructor
@Entity
@Table(name = "external_influence_levels")
public class ExternalInfluenceLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_group_id", foreignKey = @ForeignKey(name = "fk_level_property_group_id"))
    private PropertyGroup propertyGroup;

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

    public PropertyGroup getPropertyGroup() {
        return propertyGroup;
    }

    public void setPropertyGroup(PropertyGroup propertyGroup) {
        this.propertyGroup = propertyGroup;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "ExternalInfluenceLevel{", "}")
                .add("id=" + id)
                .add("name=" + name)
                .add("propertyGroup=" + propertyGroup)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalInfluenceLevel level)) return false;
        return id != null && id.equals(level.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
