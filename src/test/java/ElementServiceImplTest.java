import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementQualityDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementTypeDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ElementServiceImplTest {

    private static ElementDao elementDao;
    private static ElementTypeDao elementTypeDao;
    private static ElementQualityDao elementQualityDao;

    private static ElementServiceImpl elementService;

    private Element rootElement;

    @BeforeAll
    static void init() {
        elementDao = mock();
        elementTypeDao = mock();
        elementQualityDao = mock();
        elementService = new ElementServiceImpl(elementDao, elementTypeDao, elementQualityDao);
    }

    @BeforeEach
    void setUp() {
        // Создаем объекты без установки ID
        ElementType elementType = new ElementType();
        elementType.setName("System");

        ElementQuality elementQuality = new ElementQuality();
        elementQuality.setServiceLife(Duration.ofDays(3650L));

        rootElement = new Element();
        rootElement.setCode("ROOT001");
        rootElement.setName("Root Element");
        rootElement.setLevel(1);
        rootElement.setType(elementType);
        rootElement.setQuality(elementQuality);

        Element childElement = new Element();
        childElement.setCode("CHILD001");
        childElement.setName("Child Element");
        childElement.setLevel(2);
        childElement.setType(elementType);
        childElement.setQuality(elementQuality);
        childElement.setParent(rootElement);

        rootElement.addChild(childElement);
    }

    @Test
    void saveElement_WithChildElement_ShouldSetCorrectLevel() {
        // Arrange
        Element parent = new Element();
        parent.setLevel(1);
        parent.setCode("PARENT001");
        parent.setName("Parent Element");

        Element child = new Element();
        child.setCode("CHILD002");
        child.setName("Child Element");
        child.setParent(parent);

        // Эмулируем, что родитель существует в БД
        when(elementDao.findById(any())).thenReturn(Optional.of(parent));

        // Act
        elementService.saveElement(child);

        // Assert
        assertThat(child.getLevel()).isEqualTo(2); // Parent level + 1
    }

    @Test
    void saveElement_WithNullElement_ShouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> elementService.saveElement(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveElement_WithEmptyCode_ShouldThrowException() {
        // Arrange
        Element element = new Element();
        element.setCode("");
        element.setName("Test Element");

        // Act & Assert
        assertThatThrownBy(() -> elementService.saveElement(element))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveElement_WithNullCode_ShouldThrowException() {
        // Arrange
        Element element = new Element();
        element.setCode(null);
        element.setName("Test Element");

        // Act & Assert
        assertThatThrownBy(() -> elementService.saveElement(element))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("code");
    }

    @Test
    void updateElement_WithNonExistentElement_ShouldThrowException() {
        // Arrange
        when(elementDao.findById(any())).thenReturn(Optional.empty());

        Element nonExistentElement = new Element();
        nonExistentElement.setCode("NONEXISTENT");
        nonExistentElement.setName("Non Existent");

        // Act & Assert
        assertThatThrownBy(() -> elementService.updateElement(nonExistentElement))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteElement_WithNonExistentElement_ShouldThrowException() {
        // Arrange
        when(elementDao.findById(any())).thenReturn(Optional.empty());

        Element nonExistentElement = new Element();
        nonExistentElement.setCode("NONEXISTENT");

        // Act & Assert
        assertThatThrownBy(() -> elementService.deleteElement(nonExistentElement))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void findElementById_WithValidId_ShouldReturnElement() {
        // Arrange
        when(elementDao.findById(1L)).thenReturn(Optional.ofNullable(rootElement));

        // Act
        Optional<Element> result = elementService.findElementById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("ROOT001");
    }

    @Test
    void findElementById_WithNonExistentId_ShouldReturnEmpty() {
        // Arrange
        when(elementDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Element> result = elementService.findElementById(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findElementById_WithNullId_ShouldReturnEmpty() {
        // Act
        Optional<Element> result = elementService.findElementById(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getRootElements_ShouldReturnListOfRootElements() {
        // Arrange
        List<Element> expectedRoots = Collections.singletonList(rootElement);
        when(elementDao.findRoots()).thenReturn(expectedRoots);

        // Act
        List<Element> result = elementService.getRootElements();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("ROOT001");
    }

    @Test
    void getRootElements_WhenNoRootElements_ShouldReturnEmptyList() {
        // Arrange
        when(elementDao.findRoots()).thenReturn(Collections.emptyList());

        // Act
        List<Element> result = elementService.getRootElements();

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void getElementsTree_WithValidRootElement_ShouldReturnTreeSet() {
        // Arrange
        // Эмулируем, что элемент существует в БД и имеет правильную структуру
        when(elementDao.findById(any())).thenReturn(Optional.ofNullable(rootElement));

        // Act
        TreeSet<Element> result = elementService.getElementsTree(rootElement);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2); // Root + child
    }

    @Test
    void getElementsTree_WithNullRootElement_ShouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> elementService.getElementsTree(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root element cannot be null");
    }

    @Test
    void elementExists_WithExistingId_ShouldReturnTrue() {
        // Arrange
        when(elementDao.findById(1L)).thenReturn(Optional.ofNullable(rootElement));

        // Act
        boolean exists = elementService.findElementById(1L).isPresent();

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void elementExists_WithNonExistingId_ShouldReturnFalse() {
        // Arrange
        when(elementDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean exists = elementService.findElementById(999L).isPresent();

        // Assert
        assertThat(exists).isFalse();
    }
}
