package com.andrewaleynik.reportdesigner.reportdesigner.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elements")
public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Code cannot be blank")
    @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String code;
    @ManyToOne
    @JoinColumn(name = "type_id", foreignKey = @ForeignKey(name = "fk_type_id"))
    private ElementType type;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Element parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Element> children = new ArrayList<>();
    private Integer level;
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quality_id", foreignKey = @ForeignKey(name = "quality_id"))
    private ElementQuality quality;


    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void setChildren(List<Element> children) {
        this.children.forEach(child -> child.setParent(null));
        this.children.clear();
        if (children != null) {
            this.children.addAll(children);
            this.children.forEach(child -> {
                child.setParent(this);
            });
        }
    }

    public void addChild(Element child) {
        if (child != null && !this.children.contains(child)) {
            child.setParent(this);
            this.children.add(child);
        }
    }

    public void addChildren(List<Element> children) {
        children.forEach(this::addChild);
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public ElementQuality getQuality() {
        return this.quality;
    }

    public void setQuality(ElementQuality quality) {
        this.quality = quality;
    }
}
