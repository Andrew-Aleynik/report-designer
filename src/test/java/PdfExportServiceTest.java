import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementsTreePdfExportService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PdfExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PdfExportServiceTest {

    private ElementsTreePdfExportService elementsTreePdfExportService;
    private PdfExportService pdfExportService;

    @BeforeEach
    void setUp() {
        elementsTreePdfExportService = mock(ElementsTreePdfExportService.class);
        pdfExportService = new PdfExportService(elementsTreePdfExportService);
    }

    @Test
    void exportElementsTree_BuildsTreeAndDelegatesToExportService() {
        Element root = createRootElement();
        File expectedFile = new File("test.pdf");
        when(elementsTreePdfExportService.export(any(TreeSet.class))).thenReturn(expectedFile);

        File result = pdfExportService.exportElementsTree(root);

        assertThat(result).isSameAs(expectedFile);
        verify(elementsTreePdfExportService).export(argThat(tree ->
                tree.size() == 1 && tree.contains(root)
        ));
    }

    @Test
    void exportElementsTree_WithNullRoot_DelegatesEmptyTree() {
        File expectedFile = new File("empty.pdf");
        when(elementsTreePdfExportService.export(any(TreeSet.class))).thenReturn(expectedFile);

        File result = pdfExportService.exportElementsTree(null);

        assertThat(result).isSameAs(expectedFile);
        verify(elementsTreePdfExportService).export(argThat(TreeSet::isEmpty));
    }

    private Element createRootElement() {
        ElementType type = new ElementType();
        type.setName("System");

        Element root = new Element();
        root.setCode("ROOT001");
        root.setName("Root");
        root.setLevel(1);
        root.setType(type);
        return root;
    }
}
