import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementsTreePdfExportService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementsTreePdfExportServiceTest {

    private PropertyValueService propertyValueService;
    private ElementsTreePdfExportService exportService;

    @BeforeEach
    void setUp() {
        propertyValueService = mock(PropertyValueService.class);
        exportService = new ElementsTreePdfExportService(propertyValueService);
    }

    @Test
    void export_WithNullTree_ThrowsException() {
        assertThatThrownBy(() -> exportService.export(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не может быть пустым");
    }

    @Test
    void export_WithEmptyTree_ThrowsException() {
        assertThatThrownBy(() -> exportService.export(new TreeSet<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не может быть пустым");
    }

    @Test
    void export_WithValidTree_CreatesPdfFile(@TempDir File tempDir) {
        Element root = buildSampleTree();
        when(propertyValueService.getPropertyValueOfProperty(any())).thenReturn(Collections.emptyList());

        File pdfFile = exportService.export(ElementTreeBuilder.buildFromRoot(root));

        assertThat(pdfFile).exists();
        assertThat(pdfFile.length()).isGreaterThan(0);
        assertThat(pdfFile.getName()).endsWith(".pdf");
    }

    @Test
    void export_WithQualityAndProperties_IncludesPropertyValues() {
        Element root = buildSampleTree();
        Property property = root.getQuality().getProperties().iterator().next();

        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Heat");

        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setName("High");

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setProperty(property);
        propertyValue.setExternalInfluence(influence);
        propertyValue.setExternalInfluenceLevel(level);
        propertyValue.setValue("42");

        when(propertyValueService.getPropertyValueOfProperty(property))
                .thenReturn(List.of(propertyValue));

        File pdfFile = exportService.export(ElementTreeBuilder.buildFromRoot(root));

        assertThat(pdfFile).exists();
        assertThat(pdfFile.length()).isGreaterThan(0);
    }

    private Element buildSampleTree() {
        ElementType type = new ElementType();
        type.setName("System");

        ElementQuality quality = new ElementQuality();
        quality.setCode("Q001");
        quality.setServiceLife(new java.math.BigDecimal("365"));
        quality.setSatisfyingCost(new BigDecimal("100.00"));
        quality.setActualCost(new BigDecimal("95.00"));

        Property property = new Property();
        property.setName("Reliability");
        quality.addProperty(property);

        Element root = new Element();
        root.setCode("ROOT001");
        root.setName("Main System");
        root.setLevel(1);
        root.setType(type);
        root.setQuality(quality);
        root.setDescription("Test system");

        return root;
    }
}
