import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class ElementTreeBuilderTest {

    private Element root;
    private Element childA;
    private Element childB;

    @BeforeEach
    void setUp() {
        root = createElement("ROOT001", "Root", 1);
        childA = createElement("CHILD002", "Alpha", 2);
        childB = createElement("CHILD001", "Beta", 2);

        root.addChild(childA);
        root.addChild(childB);
    }

    @Test
    void buildFromRoot_WithNullRoot_ReturnsEmptyTree() {
        TreeSet<Element> result = ElementTreeBuilder.buildFromRoot(null);

        assertThat(result).isEmpty();
    }

    @Test
    void buildFromRoot_WithHierarchy_ContainsAllElements() {
        TreeSet<Element> result = ElementTreeBuilder.buildFromRoot(root);

        assertThat(result).containsExactly(root, childA, childB);
    }

    @Test
    void buildFromRoot_SortsByLevelThenNameThenCode() {
        Element sibling = createElement("CHILD003", "Alpha", 2);
        root.addChild(sibling);

        TreeSet<Element> result = ElementTreeBuilder.buildFromRoot(root);

        assertThat(result).containsExactly(root, childA, sibling, childB);
    }

    @Test
    void elementComparator_IsCaseInsensitiveForName() {
        Element upper = createElement("AAA001", "test", 2);
        Element lower = createElement("AAA001", "TEST", 2);

        assertThat(ElementTreeBuilder.ELEMENT_COMPARATOR.compare(upper, lower)).isZero();
    }

    private Element createElement(String code, String name, int level) {
        Element element = new Element();
        element.setCode(code);
        element.setName(name);
        element.setLevel(level);
        return element;
    }
}
