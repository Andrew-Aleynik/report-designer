import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.QualityPropertiesTableBuilder;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QualityPropertiesTableBuilderTest {

    private PropertyValueService propertyValueService;
    private QualityPropertiesTableBuilder tableBuilder;
    private PdfFont normalFont;
    private PdfFont boldFont;

    @BeforeEach
    void setUp() throws Exception {
        propertyValueService = mock(PropertyValueService.class);
        tableBuilder = new QualityPropertiesTableBuilder(propertyValueService);
        normalFont = PdfFontFactory.createFont();
        boldFont = PdfFontFactory.createFont();
    }

    @Test
    void build_WithNoPropertyValues_ReturnsTableWithThreeColumns() {
        ElementQuality quality = createQualityWithProperty("Weight");
        Property property = quality.getProperties().iterator().next();
        when(propertyValueService.getPropertyValueOfProperty(property))
                .thenReturn(Collections.emptyList());

        Table table = tableBuilder.build(quality, normalFont, boldFont, 0);

        assertThat(table).isNotNull();
        assertThat(table.getNumberOfColumns()).isEqualTo(3);
    }

    @Test
    void build_WithPropertyValues_ReturnsNonEmptyTable() {
        Property property = new Property();
        property.setName("Strength");

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.addProperty(property);

        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Vibration");

        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setName("Medium");

        PropertyValue value = new PropertyValue();
        value.setProperty(property);
        value.setExternalInfluence(influence);
        value.setExternalInfluenceLevel(level);
        value.setValue("10");

        when(propertyValueService.getPropertyValueOfProperty(property))
                .thenReturn(List.of(value));

        Table table = tableBuilder.build(quality, normalFont, boldFont, 15);

        assertThat(table).isNotNull();
        assertThat(table.getNumberOfColumns()).isEqualTo(3);
    }

    @Test
    void createSectionHeader_ReturnsFormattedParagraph() {
        Paragraph header = tableBuilder.createSectionHeader(boldFont, 30);

        assertThat(header).isNotNull();
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
