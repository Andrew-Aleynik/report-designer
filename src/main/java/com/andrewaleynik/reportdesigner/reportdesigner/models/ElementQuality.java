package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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
    @ManyToMany(mappedBy = "qualities", fetch = FetchType.EAGER)
    private Set<Property> properties = new HashSet<>();
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

    public Set<Property> getProperties() {
        return this.properties;
    }

    void internalAddProperty(Property property) {
        this.properties.add(property);
    }

    void internalRemoveProperty(Property property) {
        this.properties.remove(property);
    }

    public void addProperty(Property property) {
        if (!properties.contains(property)) {
            properties.add(property);
            property.internalAddQuality(this);
        }
    }

    public void removeProperty(Property property) {
        Iterator<Property> it = properties.iterator();
        while (it.hasNext()) {
            Property p = it.next();
            if (p.getId().equals(property.getId())) {
                it.remove();
                p.internalRemoveQuality(this);
                break;
            }
        }
    }

    public void setProperties(Collection<Property> newProperties) {
        Set<Property> toRemove = new HashSet<>(this.properties);
        toRemove.removeAll(newProperties);
        toRemove.forEach(this::removeProperty);

        Set<Property> toAdd = new HashSet<>(newProperties);
        toAdd.removeAll(this.properties);
        toAdd.forEach(this::addProperty);
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
    public String toString() {
        String propertiesString = properties.stream()
                .map(property -> Optional.of(property.getId().toString()).orElse(""))
                .collect(Collectors.joining(",", "(", ")"));
        return new StringJoiner(", ", "ElementQuality{", "}")
                .add("id=" + id)
                .add("code='" + code + '\'')
                .add("properties=" + propertiesString)
                .add("serviceLife=" + serviceLife)
                .add("satisfyingCost=" + satisfyingCost)
                .add("actualCost=" + actualCost)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementQuality quality)) return false;
        if (id != null && quality.id != null) {
            return id.equals(quality.id);
        }
        return code.equals(quality.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
