import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.ExpertReportTableBuilder;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
        Element root = createRootWithProperty("Weight");
        when(propertyValueService.getPropertyValueOfProperty(anyProperty(root)))
                .thenReturn(Collections.emptyList());

        Table table = tableBuilder.buildRealInfluencesTable(
                ElementTreeBuilder.buildFromRoot(root), normalFont, boldFont);

        assertThat(table.getNumberOfColumns()).isEqualTo(5);
    }

    @Test
    void buildRequirementsTable_HasSixColumns() {
        Element root = createRootWithProperty("Strength");
        Property property = root.getQuality().getProperties().iterator().next();

        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Vibration");
        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setName("Medium");
        PropertyValue value = new PropertyValue();
        value.setProperty(property);
        value.setExternalInfluence(influence);
        value.setExternalInfluenceLevel(level);
        value.setValue("10");

        when(propertyValueService.getPropertyValueOfProperty(property)).thenReturn(List.of(value));

        Table table = tableBuilder.buildRequirementsTable(
                ElementTreeBuilder.buildFromRoot(root), normalFont, boldFont);

        assertThat(table.getNumberOfColumns()).isEqualTo(6);
    }

    private Element createRootWithProperty(String propertyName) {
        Property property = new Property();
        property.setName(propertyName);
        property.setQualityCriterionValue("не менее 1");

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.addProperty(property);

        Element root = new Element();
        root.setCode("ROOT001");
        root.setName("Main System");
        root.setLevel(1);
        root.setQuality(quality);
        return root;
    }

    private Property anyProperty(Element root) {
        return root.getQuality().getProperties().iterator().next();
    }
}
