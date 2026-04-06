package com.andrewaleynik.reportdesigner.reportdesigner.domains;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class PropertyValueDomain {

    public record Pair(Long id, String value) {
    }

    private final Property property;
    private ExternalInfluence externalInfluence;
    private final Map<ExternalInfluenceLevel, Pair> levelValues = new HashMap<>();

    public PropertyValueDomain(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    public ExternalInfluence getExternalInfluence() {
        return externalInfluence;
    }

    public void setExternalInfluence(ExternalInfluence externalInfluence) {
        this.externalInfluence = externalInfluence;
    }

    public Pair getLevelPair(ExternalInfluenceLevel level) {
        return levelValues.getOrDefault(level, new Pair(null, ""));
    }

    public void setLevelPair(ExternalInfluenceLevel level, Long id, String value) {
        if (value == null || value.isBlank()) {
            levelValues.remove(level);
        } else {
            levelValues.put(level, new Pair(id, value));
        }
    }

    public void setLevelPairValue(ExternalInfluenceLevel level, String value) {
        if (value == null || value.isBlank()) {
            levelValues.remove(level);
        } else {
            Pair pair = levelValues.getOrDefault(level, new Pair(null, ""));
            levelValues.put(level, new Pair(pair.id, value));
        }
    }

    public void setLevelPairId(ExternalInfluenceLevel level, Long id) {
        Pair pair = levelValues.getOrDefault(level, new Pair(null, ""));
        levelValues.put(level, new Pair(id, pair.value));
    }

    public Map<ExternalInfluenceLevel, Pair> getAllLevelPairs() {
        return new HashMap<>(levelValues);
    }

    public String getPropertyDisplay() {
        return property != null ? (property.getName() + "(" + property.getUnit().getName() + ")") : "";
    }

    public String getInfluenceDisplay() {
        return externalInfluence != null ? externalInfluence.getName() : "";
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "PropertyValueDomain{", "}")
                .add("property=" + property)
                .add("externalInfluence=" + externalInfluence)
                .add("levelValues=" + levelValues)
                .toString();
    }
}