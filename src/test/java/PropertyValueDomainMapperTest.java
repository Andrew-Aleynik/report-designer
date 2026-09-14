import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomainMapper;
import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyValueDomainMapperTest {

    @Test
    void fromQuality_WithNoPropertyValues_ReturnsEmptyRows() {
        ElementQuality quality = createQualityWithProperty("Weight");

        List<PropertyValueDomain> rows = PropertyValueDomainMapper.fromQuality(quality, Collections.emptyList());

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getExternalInfluence()).isNull();
    }

    @Test
    void fromQuality_WithMatchingPropertyValue_MapsInfluenceAndLevel() {
        Property property = new Property();
        property.setName("Strength");

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.addProperty(property);

        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Heat");

        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setName("High");

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setId(1L);
        propertyValue.setProperty(property);
        propertyValue.setExternalInfluence(influence);
        propertyValue.setExternalInfluenceLevel(level);
        propertyValue.setValue("42");

        List<PropertyValueDomain> rows = PropertyValueDomainMapper.fromQuality(quality, List.of(propertyValue));

        assertThat(rows).hasSize(1);
        PropertyValueDomain row = rows.get(0);
        assertThat(row.getExternalInfluence()).isSameAs(influence);
        assertThat(row.getLevelPair(level).value()).isEqualTo("42");
        assertThat(row.getLevelPair(level).id()).isEqualTo(1L);
    }

    private ElementQuality createQualityWithProperty(String propertyName) {
        Property property = new Property();
        property.setName(propertyName);

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.addProperty(property);
        return quality;
    }
}
