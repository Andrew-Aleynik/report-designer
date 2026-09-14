import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyValueDomainTest {

    @Test
    void getPropertyDisplay_WithUnit_IncludesUnitName() {
        Property property = new Property();
        property.setName("Temperature");

        PropertyUnit unit = new PropertyUnit();
        unit.setName("C");
        property.setUnit(unit);

        PropertyValueDomain domain = new PropertyValueDomain(property);

        assertThat(domain.getPropertyDisplay()).isEqualTo("Temperature (C)");
    }

    @Test
    void getPropertyDisplay_WithoutUnit_UsesPlaceholder() {
        Property property = new Property();
        property.setName("Pressure");

        PropertyValueDomain domain = new PropertyValueDomain(property);

        assertThat(domain.getPropertyDisplay()).isEqualTo("Pressure (-)");
    }
}
