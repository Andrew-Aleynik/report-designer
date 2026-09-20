import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.ElementDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.ElementQualityDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.ElementTypeDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.ExternalInfluenceDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.ExternalInfluenceGroupDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.ExternalInfluenceLevelDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.PropertyDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.PropertyGroupDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.PropertyUnitDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.PropertyValueDaoImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementsTreePdfExportService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueServiceImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fills the database with a realistic sample model and exports a PDF report.
 * The generated file is copied to {@code target/sample-reports/expert-report.pdf}
 * so it can be opened and visually inspected.
 */
class SampleReportExportTest extends BaseTest {

    private ElementDaoImpl elementDao;
    private ElementTypeDaoImpl elementTypeDao;
    private ElementQualityDaoImpl elementQualityDao;
    private PropertyDaoImpl propertyDao;
    private PropertyGroupDaoImpl propertyGroupDao;
    private PropertyUnitDaoImpl propertyUnitDao;
    private PropertyValueDaoImpl propertyValueDao;
    private ExternalInfluenceDaoImpl externalInfluenceDao;
    private ExternalInfluenceGroupDaoImpl externalInfluenceGroupDao;
    private ExternalInfluenceLevelDaoImpl externalInfluenceLevelDao;

    private ElementsTreePdfExportService exportService;

    @BeforeEach
    void initDaos() {
        elementDao = new ElementDaoImpl();
        elementTypeDao = new ElementTypeDaoImpl();
        elementQualityDao = new ElementQualityDaoImpl();
        propertyDao = new PropertyDaoImpl();
        propertyGroupDao = new PropertyGroupDaoImpl();
        propertyUnitDao = new PropertyUnitDaoImpl();
        propertyValueDao = new PropertyValueDaoImpl();
        externalInfluenceDao = new ExternalInfluenceDaoImpl();
        externalInfluenceGroupDao = new ExternalInfluenceGroupDaoImpl();
        externalInfluenceLevelDao = new ExternalInfluenceLevelDaoImpl();

        exportService = new ElementsTreePdfExportService(new PropertyValueServiceImpl(propertyValueDao));
    }

    @Test
    void fillDatabaseAndExportSampleReport() throws Exception {
        Element root = seedSampleData();

        Element persistedRoot = elementDao.findById(root.getId()).orElseThrow();
        TreeSet<Element> tree = ElementTreeBuilder.buildFromRoot(persistedRoot);

        File tempPdf = exportService.export(tree);
        assertThat(tempPdf).exists().isFile();
        assertThat(tempPdf.length()).isGreaterThan(1000);

        Path outputDir = Path.of("target", "sample-reports");
        Files.createDirectories(outputDir);
        Path outputPdf = outputDir.resolve("expert-report.pdf");
        Files.copy(tempPdf.toPath(), outputPdf, StandardCopyOption.REPLACE_EXISTING);

        assertThat(outputPdf).exists();
        assertThat(Files.size(outputPdf)).isGreaterThan(1000);

        System.out.println("Sample report saved to: " + outputPdf.toAbsolutePath());
    }

    private Element seedSampleData() {
        ElementType systemType = saveType("Система");
        ElementType componentType = saveType("Компонент");

        PropertyGroup surfaceGroup = savePropertyGroup("Внешняя дефектность");
        PropertyGroup geometryGroup = savePropertyGroup("Геометрические размеры");

        PropertyUnit appearance = saveUnit("Внешний вид");
        PropertyUnit mm = saveUnit("мм");
        PropertyUnit mkmPerHour = saveUnit("мкм/ч, не более");

        Property surfaceDefects = createProperty(
                "Внешняя дефектность", "Отсутствие пропусков, пузырей, отслоений", appearance, surfaceGroup);
        Property adhesion = createProperty(
                "Адгезия", "не менее 5 МПа / или характер разрушения", mm, surfaceGroup);
        Property thickness = createProperty(
                "Геометрические размеры", "не менее 0,35 мм", mm, geometryGroup);
        Property wearRate = createProperty(
                "Скорость изменения толщины покрытия", "0,003 мкм/ч", mkmPerHour, geometryGroup);

        propertyDao.save(surfaceDefects);
        propertyDao.save(adhesion);
        propertyDao.save(thickness);
        propertyDao.save(wearRate);

        ElementQuality quality = new ElementQuality();
        quality.setCode("HTPTS-DEMO");
        quality.setServiceLife(new java.math.BigDecimal("3650"));
        quality.setSatisfyingCost(new BigDecimal("125000.00"));
        quality.setActualCost(new BigDecimal("118500.00"));
        quality.addProperty(surfaceDefects);
        quality.addProperty(adhesion);
        quality.addProperty(thickness);
        quality.addProperty(wearRate);
        elementQualityDao.save(quality);

        ExternalInfluenceGroup climateGroup = saveInfluenceGroup("Климатические");
        ExternalInfluenceGroup mechanicalGroup = saveInfluenceGroup("Механические");

        ExternalInfluence initialState = saveInfluence(
                "Исходное состояние",
                "В исходном состоянии при температуре 20°C",
                climateGroup);
        ExternalInfluence thermalCycling = saveInfluence(
                "Термоциклирование",
                "Термоциклирование от минус 50°C до плюс 70°C",
                climateGroup);
        ExternalInfluence waterFlow = saveInfluence(
                "Поток водной среды",
                "Воздействие потока водной среды (3% раствор NaCl)",
                mechanicalGroup);

        ExternalInfluenceLevel temperature = saveLevel("температура", surfaceGroup);
        ExternalInfluenceLevel humidity = saveLevel("влажность", surfaceGroup);
        ExternalInfluenceLevel pressure = saveLevel("давление", geometryGroup);
        ExternalInfluenceLevel flowTemp = saveLevel("температура среды", geometryGroup);

        saveValue(surfaceDefects, initialState, temperature, "20°C");
        saveValue(surfaceDefects, thermalCycling, temperature, "от -50°C до +70°C");
        saveValue(surfaceDefects, thermalCycling, humidity, "не нормируется");
        saveValue(adhesion, initialState, temperature, "20°C");
        saveValue(thickness, waterFlow, pressure, "10 МПа");
        saveValue(thickness, waterFlow, flowTemp, "60°C");
        saveValue(wearRate, waterFlow, pressure, "10 МПа");
        saveValue(wearRate, waterFlow, flowTemp, "60°C");

        Element root = new Element();
        root.setCode("DEMO-ROOT");
        root.setName("Внутреннее покрытие водоводов");
        root.setDescription("Демонстрационная структурная модель для проверки отчёта");
        root.setLevel(1);
        root.setType(systemType);
        root.setQuality(quality);

        Element child = new Element();
        child.setCode("DEMO-CHILD");
        child.setName("Защитный слой");
        child.setDescription("Дочерний элемент с тем же качеством (не должен дублировать строки отчёта)");
        child.setLevel(2);
        child.setType(componentType);
        child.setQuality(quality);

        root.addChild(child);
        elementDao.save(root);

        return root;
    }

    private ElementType saveType(String name) {
        ElementType type = new ElementType();
        type.setName(name);
        elementTypeDao.save(type);
        return type;
    }

    private PropertyGroup savePropertyGroup(String name) {
        PropertyGroup group = new PropertyGroup();
        group.setName(name);
        propertyGroupDao.save(group);
        return group;
    }

    private PropertyUnit saveUnit(String name) {
        PropertyUnit unit = new PropertyUnit();
        unit.setName(name);
        propertyUnitDao.save(unit);
        return unit;
    }

    private Property createProperty(String name,
                                    String criterion,
                                    PropertyUnit unit,
                                    PropertyGroup group) {
        Property property = new Property();
        property.setName(name);
        property.setQualityCriterionValue(criterion);
        property.setUnit(unit);
        property.setPropertyGroup(group);
        return property;
    }

    private ExternalInfluenceGroup saveInfluenceGroup(String name) {
        ExternalInfluenceGroup group = new ExternalInfluenceGroup();
        group.setName(name);
        externalInfluenceGroupDao.save(group);
        return group;
    }

    private ExternalInfluence saveInfluence(String name,
                                            String description,
                                            ExternalInfluenceGroup group) {
        ExternalInfluence influence = new ExternalInfluence();
        influence.setName(name);
        influence.setDescription(description);
        influence.setExternalInfluenceGroup(group);
        externalInfluenceDao.save(influence);
        return influence;
    }

    private ExternalInfluenceLevel saveLevel(String name, PropertyGroup propertyGroup) {
        ExternalInfluenceLevel level = new ExternalInfluenceLevel();
        level.setName(name);
        level.setPropertyGroup(propertyGroup);
        externalInfluenceLevelDao.save(level);
        return level;
    }

    private void saveValue(Property property,
                           ExternalInfluence influence,
                           ExternalInfluenceLevel level,
                           String value) {
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setProperty(property);
        propertyValue.setExternalInfluence(influence);
        propertyValue.setExternalInfluenceLevel(level);
        propertyValue.setValue(value);
        propertyValueDao.save(propertyValue);
    }
}
