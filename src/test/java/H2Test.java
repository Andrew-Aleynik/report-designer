import com.andrewaleynik.reportdesigner.reportdesigner.dao.*;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.*;
import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class H2Test extends BaseTest {

    private ElementDao elementDao;
    private ElementTypeDao elementTypeDao;
    private ElementQualityDao elementQualityDao;
    private PropertyUnitDao propertyUnitDao;
    private PropertyDao propertyDao;
    private ExternalInfluenceDao externalInfluenceDao;

    @BeforeEach
    void setUp() {
        super.setUp();
        elementDao = new ElementDaoImpl();
        elementTypeDao = new ElementTypeDaoImpl();
        elementQualityDao = new ElementQualityDaoImpl();
        propertyUnitDao = new PropertyUnitDaoImpl();
        propertyDao = new PropertyDaoImpl();
        externalInfluenceDao = new ExternalInfluenceDaoImpl();
    }

    @Test
    void testElementCRUDOperations() {
        ElementType type = createElementType("Система");
        elementTypeDao.save(type);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element element = new Element();
        element.setCode("SYS001");
        element.setName("Основная система");
        element.setDescription("Главная система проекта");
        element.setLevel(1);
        element.setType(type);
        element.setQuality(quality);

        elementDao.save(element);

        assertPersisted(element);
        assertThat(element.getId()).isNotNull();

        Optional<Element> foundElementOptional = elementDao.findById(element.getId());
        assertThat(foundElementOptional).isPresent();

        Element foundElement = foundElementOptional.get();
        assertThat(foundElement.getCode()).isEqualTo("SYS001");
        assertThat(foundElement.getName()).isEqualTo("Основная система");
        assertThat(foundElement.getLevel()).isEqualTo(1);
        assertThat(foundElement.getType().getName()).isEqualTo("Система");

        foundElement.setName("Обновленная система");
        foundElement.setDescription("Новое описание");
        elementDao.update(foundElement);

        Optional<Element> updatedElementOptional = elementDao.findById(element.getId());
        assertThat(updatedElementOptional).isPresent();

        Element updatedElement = updatedElementOptional.get();
        assertThat(updatedElement.getName()).isEqualTo("Обновленная система");
        assertThat(updatedElement.getDescription()).isEqualTo("Новое описание");

        elementDao.delete(element);

        Optional<Element> deletedElementOptional = elementDao.findById(element.getId());

        assertThat(deletedElementOptional).isEmpty();
    }

    @Test
    void testElementHierarchy() {
        ElementType type = createElementType("Система");
        elementTypeDao.save(type);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element parent = new Element();
        parent.setCode("PARENT001");
        parent.setName("Родительский элемент");
        parent.setLevel(1);
        parent.setType(type);
        parent.setQuality(quality);

        Element child = new Element();
        child.setCode("CHILD001");
        child.setName("Дочерний элемент");
        child.setLevel(2);
        child.setType(type);
        child.setQuality(quality);

        elementDao.save(parent);
        parent.addChild(child);
        elementDao.update(parent);

        List<Element> children = elementDao.findByParent(parent);
        assertThat(children).hasSize(1);
        assertThat(children.get(0).getCode()).isEqualTo("CHILD001");
        assertThat(children.get(0).getLevel()).isEqualTo(2);
        assertThat(children.get(0).getParent().getId()).isEqualTo(parent.getId());
    }

    @Test
    void testFindElementByCode() {
        ElementType type = createElementType("Компонент");
        elementTypeDao.save(type);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element element = new Element();
        element.setCode("UNIQUE_CODE_123");
        element.setName("Тестовый элемент");
        element.setLevel(1);
        element.setType(type);
        element.setQuality(quality);
        elementDao.save(element);

        Optional<Element> found = elementDao.findByCode("UNIQUE_CODE_123");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Тестовый элемент");
        assertThat(elementDao.existsByCode("UNIQUE_CODE_123")).isTrue();
        assertThat(elementDao.existsByCode("NON_EXISTENT_CODE")).isFalse();
    }

    @Test
    void testElementQualityWithProperties() {
        ElementQuality quality = new ElementQuality();
        quality.setCode("HTPTS");
        quality.setServiceLife(Duration.ofDays(3650));
        quality.setSatisfyingCost(new BigDecimal("100000.00"));
        quality.setActualCost(new BigDecimal("95000.00"));

        PropertyUnit unit = new PropertyUnit();
        unit.setName("шт.");
        propertyUnitDao.save(unit);

        Property property = new Property();
        property.setCurrentValue("100");
        property.setQualityCriterionValue("200");
        property.setUnit(unit);
        quality.addProperty(property);
        elementQualityDao.save(quality);
        propertyDao.save(property);

        Optional<ElementQuality> foundQualityOptional = elementQualityDao.findById(quality.getId());
        assertThat(foundQualityOptional).isPresent();
        ElementQuality foundQuality = foundQualityOptional.get();
        assertThat(foundQuality.getProperties()).hasSize(1);
        assertThat(foundQuality.getServiceLife().toDays()).isEqualTo(3650);
        assertThat(foundQuality.getSatisfyingCost()).isEqualByComparingTo("100000.00");

        Property foundProperty = foundQuality.getProperties().iterator().next();
        assertThat(foundProperty.getUnit().getName()).isEqualTo("шт.");
        assertThat(foundProperty.getCurrentValue()).isEqualTo("100");
        assertThat(foundProperty.getQualityCriterionValue()).isEqualTo("200");
    }

    @Test
    void testFindElementsByType() {
        ElementType systemType = createElementType("Система");
        ElementType componentType = createElementType("Компонент");
        elementTypeDao.save(systemType);
        elementTypeDao.save(componentType);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element systemElement = new Element();
        systemElement.setCode("SYS001");
        systemElement.setName("Система А");
        systemElement.setLevel(1);
        systemElement.setType(systemType);
        systemElement.setQuality(quality);

        Element componentElement = new Element();
        componentElement.setCode("COMP001");
        componentElement.setName("Компонент Б");
        componentElement.setLevel(2);
        componentElement.setType(componentType);
        componentElement.setQuality(quality);

        elementDao.save(systemElement);
        elementDao.save(componentElement);

        List<Element> systems = elementDao.findByType(systemType);
        List<Element> components = elementDao.findByType(componentType);

        assertThat(systems).hasSize(1);
        assertThat(systems.get(0).getCode()).isEqualTo("SYS001");

        assertThat(components).hasSize(1);
        assertThat(components.get(0).getCode()).isEqualTo("COMP001");
    }

    @Test
    void testExternalInfluenceCRUD() {
        ExternalInfluence influence = new ExternalInfluence();
        influence.setName("Температура");
        influence.setDescription("Влияние температурных условий на элементы системы");

        externalInfluenceDao.save(influence);

        Optional<ExternalInfluence> foundOptional = externalInfluenceDao.findById(influence.getId());
        assertThat(foundOptional).isPresent();
        ExternalInfluence found = foundOptional.get();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Температура");
        assertThat(found.getDescription()).contains("температурных условий");
    }

    @Test
    void testRootElements() {
        ElementType type = createElementType("Система");
        elementTypeDao.save(type);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element rootElement = new Element();
        rootElement.setCode("ROOT001");
        rootElement.setName("Корневой элемент");
        rootElement.setLevel(1);
        rootElement.setType(type);
        rootElement.setQuality(quality);

        Element childElement = new Element();
        childElement.setCode("CHILD001");
        childElement.setName("Дочерний элемент");
        childElement.setLevel(2);
        childElement.setType(type);
        childElement.setQuality(quality);
        childElement.setParent(rootElement);

        elementDao.save(rootElement);
        elementDao.save(childElement);

        List<Element> rootElements = elementDao.findRoots();

        assertThat(rootElements).hasSize(1);
        assertThat(rootElements.get(0).getCode()).isEqualTo("ROOT001");
        assertThat(rootElements.get(0).getParent()).isNull();
    }

    @Test
    void testElementLevelManagement() {
        ElementType type = createElementType("Уровень");
        elementTypeDao.save(type);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element element = new Element();
        element.setCode("LEVEL_TEST");
        element.setName("Тест уровней");
        element.setType(type);
        element.setQuality(quality);

        element.setLevel(1);
        elementDao.save(element);

        Optional<Element> foundOptional = elementDao.findById(element.getId());
        assertThat(foundOptional).isPresent();
        Element found = foundOptional.get();
        assertThat(found.getLevel()).isEqualTo(1);

        List<Element> level1Elements = elementDao.findByLevel(1);

        assertThat(level1Elements).hasSize(1);
        assertThat(level1Elements.get(0).getCode()).isEqualTo("LEVEL_TEST");
    }

    @Test
    void testDeleteElement() {
        ElementType type = createElementType("Удаление");
        elementTypeDao.save(type);

        ElementQuality quality = createElementQuality();
        elementQualityDao.save(quality);

        Element element = new Element();
        element.setCode("DELETE_TEST");
        element.setName("Тест удаления");
        element.setType(type);
        element.setQuality(quality);
        element.setLevel(0);

        Element child = new Element();
        child.setCode("DELETE_TEST1");
        child.setName("Тест удаления 1");
        child.setType(type);
        child.setQuality(quality);
        child.setLevel(1);

        element.addChild(child);
        child.setParent(element);
        elementDao.save(element);
        Long elementId = element.getId();
        Long childId = child.getId();
        child.getParent().getChildren().remove(child);
        child.setParent(null);
        elementDao.update(element);
        Optional<Element> foundOptional = elementDao.findById(elementId);
        assertThat(foundOptional).isPresent();
        foundOptional = elementDao.findById(childId);
        assertThat(foundOptional).isEmpty();
    }

    private ElementType createElementType(String name) {
        ElementType type = new ElementType();
        type.setName(name);
        return type;
    }

    private ElementQuality createElementQuality() {
        ElementQuality quality = new ElementQuality();
        quality.setCode("HTPT");
        quality.setServiceLife(Duration.ofDays(3650));
        quality.setSatisfyingCost(new BigDecimal("75000.00"));
        quality.setActualCost(new BigDecimal("72000.00"));
        return quality;
    }
}
