package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@Entity
@Table(name = "types")
public class ElementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    @Column(unique = true, nullable = false)
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementType type = (ElementType) o;

        if (id != null && type.id != null) {
            return id.equals(type.id);
        }

        return name.equals(type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
