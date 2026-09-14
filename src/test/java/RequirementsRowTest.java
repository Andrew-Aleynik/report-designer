import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.RequirementsRow;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RequirementsRowTest {

    @Test
    void fromProperty_WithNoValues_CreatesSinglePlaceholderRow() {
        Property property = new Property();
        property.setName("Толщина");
        property.setQualityCriterionValue("не менее 0,35 мм");

        List<RequirementsRow> rows = RequirementsRow.fromProperty(1, property, Collections.emptyList());

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).number()).isEqualTo("1.1");
        assertThat(rows.get(0).propertyAndInfluence()).contains("Толщина");
        assertThat(rows.get(0).norm()).isEqualTo("не менее 0,35 мм");
    }

    @Test
    void fromProperty_WithInfluence_FormatsExpertStyleColumns() {
        Property property = new Property();
        property.setName("Внешняя дефектность");
        PropertyUnit unit = new PropertyUnit();
        unit.setName("Внешний вид");
        property.setUnit(unit);
        property.setQualityCriterionValue("Отсутствие пропусков");

        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Исходное состояние");
        influence.setDescription("В исходном состоянии при температуре 20°С");

        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setId(1L);
        level.setName("температура");

        PropertyValue value = new PropertyValue();
        value.setProperty(property);
        value.setExternalInfluence(influence);
        value.setExternalInfluenceLevel(level);
        value.setValue("20°С");

        List<RequirementsRow> rows = RequirementsRow.fromProperty(1, property, List.of(value));

        assertThat(rows).hasSize(1);
        RequirementsRow row = rows.get(0);
        assertThat(row.number()).isEqualTo("1.1");
        assertThat(row.propertyAndInfluence())
                .isEqualTo("Внешняя дефектность: В исходном состоянии при температуре 20°С");
        assertThat(row.testRegime()).isEqualTo("температура 20°С");
        assertThat(row.propertyIndicator()).isEqualTo("Внешний вид");
        assertThat(row.norm()).isEqualTo("Отсутствие пропусков");
        assertThat(row.controlMethod()).isEqualTo("—");
    }

    @Test
    void fromProperty_DeduplicatesLevelsAndContinuesGroupNumbering() {
        PropertyGroup group = new PropertyGroup();
        group.setId(1L);
        group.setName("Геометрия");

        Property property = new Property();
        property.setName("Толщина покрытия");
        property.setPropertyGroup(group);

        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Поток");
        influence.setDescription("Воздействие потока водной среды");

        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setId(10L);
        level.setName("давление");

        PropertyValue first = new PropertyValue();
        first.setProperty(property);
        first.setExternalInfluence(influence);
        first.setExternalInfluenceLevel(level);
        first.setValue("10МПа");

        PropertyValue duplicate = new PropertyValue();
        duplicate.setProperty(property);
        duplicate.setExternalInfluence(influence);
        duplicate.setExternalInfluenceLevel(level);
        duplicate.setValue("10МПа");

        AtomicInteger rowIndex = new AtomicInteger(2);
        List<RequirementsRow> rows = RequirementsRow.fromProperty(
                2, rowIndex, property, List.of(first, duplicate));

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).number()).isEqualTo("2.2");
        assertThat(rows.get(0).testRegime()).isEqualTo("давление 10МПа");
        assertThat(rowIndex.get()).isEqualTo(3);
    }
}
