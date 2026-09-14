import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomainMapper;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyGroupTablesTest {

    @Test
    void fromQuality_GroupsRowsByPropertyGroup() {
        PropertyGroup geometry = new PropertyGroup();
        geometry.setId(1L);
        geometry.setName("Геометрия");

        PropertyGroup surface = new PropertyGroup();
        surface.setId(2L);
        surface.setName("Поверхность");

        Property width = new Property();
        width.setName("Ширина");
        width.setPropertyGroup(geometry);

        Property roughness = new Property();
        roughness.setName("Шероховатость");
        roughness.setPropertyGroup(surface);

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.addProperty(width);
        quality.addProperty(roughness);

        List<PropertyValueDomain> rows =
                PropertyValueDomainMapper.fromQuality(quality, Collections.emptyList());

        Map<PropertyGroup, List<PropertyValueDomain>> byGroup = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.getProperty().getPropertyGroup(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        assertThat(byGroup).hasSize(2);
        assertThat(byGroup.get(geometry)).extracting(r -> r.getProperty().getName())
                .containsExactly("Ширина");
        assertThat(byGroup.get(surface)).extracting(r -> r.getProperty().getName())
                .containsExactly("Шероховатость");
        assertThat(byGroup.keySet().stream().map(PropertyGroup::getName).toList())
                .containsExactlyInAnyOrder("Геометрия", "Поверхность");
        assertThat(byGroup.keySet().stream().noneMatch(Objects::isNull)).isTrue();
    }
}
