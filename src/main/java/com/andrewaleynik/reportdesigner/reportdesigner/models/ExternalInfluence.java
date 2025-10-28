package com.andrewaleynik.reportdesigner.reportdesigner.models;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@NoArgsConstructor
@Entity
@Table(name = "external_influences")
public class ExternalInfluence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
