package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * UI/PDF labels for optimal-quality cost fields.
 */
public final class QualityCostsFormatter {

    public static final String PRODUCER_COST_LABEL = "Допустимые затраты производителя";
    public static final String CONSUMER_COST_LABEL = "Допустимые затраты потребителя";
    public static final String OTHER_STAKEHOLDERS_COST_LABEL =
            "Допустимые затраты других заинтересованных сторон";

    private QualityCostsFormatter() {
    }

    public static List<String> formatLines(ElementQuality quality) {
        List<String> lines = new ArrayList<>(3);
        if (quality == null) {
            return lines;
        }
        lines.add(formatLine(PRODUCER_COST_LABEL, quality.getServiceLife()));
        lines.add(formatLine(CONSUMER_COST_LABEL, quality.getSatisfyingCost()));
        lines.add(formatLine(OTHER_STAKEHOLDERS_COST_LABEL, quality.getActualCost()));
        return lines;
    }

    private static String formatLine(String label, BigDecimal value) {
        return label + ": " + (value != null ? value.toPlainString() : "—");
    }
}
