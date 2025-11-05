package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor

@Entity
@Table(name = "qualities")
public class ElementQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Code cannot be blank")
    @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String code;
    @OneToMany(mappedBy = "quality", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();
    private Duration serviceLife;
    private BigDecimal satisfyingCost;
    private BigDecimal actualCost;

    public Long getId() {
        return this.id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties.forEach(property -> property.setQuality(null));
        this.properties.clear();
        if (properties != null) {
            this.properties.addAll(properties);
            this.properties.forEach(property -> property.setQuality(this));
        }
    }

    public void addProperties(List<Property> properties) {
        this.properties.addAll(properties);
        properties.forEach(property -> property.setQuality(this));
    }

    public void addProperty(Property property) {
        this.properties.add(property);
        property.setQuality(this);
    }

    public Duration getServiceLife() {
        return this.serviceLife;
    }

    public void setServiceLife(Duration serviceLife) {
        this.serviceLife = serviceLife;
    }

    public BigDecimal getSatisfyingCost() {
        return this.satisfyingCost;
    }

    public void setSatisfyingCost(BigDecimal satisfyingCost) {
        this.satisfyingCost = satisfyingCost;
    }

    public BigDecimal getActualCost() {
        return this.actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementQuality quality = (ElementQuality) o;

        if (id != null && quality.id != null) {
            return id.equals(quality.id);
        }

        return code.equals(quality.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
