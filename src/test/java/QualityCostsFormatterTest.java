import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.QualityCostsFormatter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class QualityCostsFormatterTest {

    @Test
    void formatLines_UsesProducerConsumerAndOtherStakeholderLabels() {
        ElementQuality quality = new ElementQuality();
        quality.setServiceLife(new BigDecimal("1000"));
        quality.setSatisfyingCost(new BigDecimal("2000.50"));
        quality.setActualCost(new BigDecimal("3000"));

        assertThat(QualityCostsFormatter.formatLines(quality)).containsExactly(
                "Допустимые затраты производителя: 1000",
                "Допустимые затраты потребителя: 2000.50",
                "Допустимые затраты других заинтересованных сторон: 3000"
        );
    }

    @Test
    void formatLines_WithMissingValues_UsesDash() {
        assertThat(QualityCostsFormatter.formatLines(new ElementQuality())).containsExactly(
                "Допустимые затраты производителя: —",
                "Допустимые затраты потребителя: —",
                "Допустимые затраты других заинтересованных сторон: —"
        );
    }
}
