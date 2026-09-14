import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.PropertyDisplayFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyDisplayFormatterTest {

    @Test
    void format_WithNameOnly_ReturnsName() {
        Property property = new Property();
        property.setName("Temperature");

        assertThat(PropertyDisplayFormatter.format(property)).isEqualTo("Temperature");
    }

    @Test
    void format_WithNullName_ReturnsPlaceholder() {
        Property property = new Property();

        assertThat(PropertyDisplayFormatter.format(property)).isEqualTo("Без названия");
    }

    @Test
    void format_WithUnitAndCriterion_IncludesAllParts() {
        Property property = new Property();
        property.setName("Pressure");

        PropertyUnit unit = new PropertyUnit();
        unit.setName("Pa");
        property.setUnit(unit);
        property.setQualityCriterionValue(">= 100");

        String result = PropertyDisplayFormatter.format(property);

        assertThat(result)
                .contains("Pressure")
                .contains("Ед.изм.: Pa")
                .contains("Критерий: >= 100");
    }
}
