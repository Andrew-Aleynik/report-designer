import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.ExpertReportTableBuilder;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpertReportTableBuilderTest {

    private PropertyValueService propertyValueService;
    private ExpertReportTableBuilder tableBuilder;
    private PdfFont normalFont;
    private PdfFont boldFont;

    @BeforeEach
    void setUp() throws Exception {
        propertyValueService = mock(PropertyValueService.class);
        tableBuilder = new ExpertReportTableBuilder(propertyValueService);
        normalFont = PdfFontFactory.createFont();
        boldFont = PdfFontFactory.createFont();
    }

    @Test
    void buildRealInfluencesTable_HasFiveColumns() {
        Element root = createRootWithSharedQuality();
        when(propertyValueService.getPropertyValueOfProperty(anyProperty(root)))
                .thenReturn(List.of());

        Table table = tableBuilder.buildRealInfluencesTable(
                ElementTreeBuilder.buildFromRoot(root), normalFont, boldFont);

        assertThat(table.getNumberOfColumns()).isEqualTo(5);
    }

    @Test
    void buildRequirementsTable_HasSixColumns() {
        Element root = createRootWithSharedQuality();
        Property property = anyProperty(root);
        when(propertyValueService.getPropertyValueOfProperty(property))
                .thenReturn(List.of(sampleValue(property)));

        Table table = tableBuilder.buildRequirementsTable(
                ElementTreeBuilder.buildFromRoot(root), normalFont, boldFont);

        assertThat(table.getNumberOfColumns()).isEqualTo(6);
    }

    @Test
    void buildRequirementsTable_DoesNotDuplicateSharedQualityProperties() {
        Element root = createRootWithSharedQuality();
        Property property = anyProperty(root);
        when(propertyValueService.getPropertyValueOfProperty(property))
                .thenReturn(List.of(sampleValue(property)));

        tableBuilder.buildRequirementsTable(
                ElementTreeBuilder.buildFromRoot(root), normalFont, boldFont);

        verify(propertyValueService, times(1)).getPropertyValueOfProperty(property);
    }

    private Element createRootWithSharedQuality() {
        PropertyGroup group = new PropertyGroup();
        group.setId(1L);
        group.setName("Внешняя дефектность");

        PropertyUnit unit = new PropertyUnit();
        unit.setName("Внешний вид");

        Property property = new Property();
        property.setName("Адгезия");
        property.setPropertyGroup(group);
        property.setUnit(unit);
        property.setQualityCriterionValue("не менее 5");

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.addProperty(property);

        Element root = new Element();
        root.setCode("ROOT001");
        root.setName("Внутреннее покрытие водоводов");
        root.setLevel(1);
        root.setQuality(quality);

        Element child = new Element();
        child.setCode("CHILD001");
        child.setName("Слой покрытия");
        child.setLevel(2);
        child.setQuality(quality);
        root.addChild(child);

        return root;
    }

    private PropertyValue sampleValue(Property property) {
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
        return value;
    }

    private Property anyProperty(Element root) {
        return root.getQuality().getProperties().iterator().next();
    }
}
