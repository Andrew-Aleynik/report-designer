import com.andrewaleynik.reportdesigner.reportdesigner.config.ApplicationContext;
import com.andrewaleynik.reportdesigner.reportdesigner.config.ControllerFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.controllers.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ControllerFactoryTest {

    private final ControllerFactory controllerFactory =
            new ControllerFactory(ApplicationContext.getInstance());

    @Test
    void call_WithKnownController_ReturnsInstance() {
        Object controller = controllerFactory.call(ElementsTreeTabController.class);

        assertThat(controller).isInstanceOf(ElementsTreeTabController.class);
    }

    @Test
    void call_WithPreviewController_ReturnsInstance() {
        Object controller = controllerFactory.call(PreviewController.class);

        assertThat(controller).isInstanceOf(PreviewController.class);
    }

    @Test
    void call_WithUnknownController_ThrowsException() {
        assertThatThrownBy(() -> controllerFactory.call(Object.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown controller");
    }
}
