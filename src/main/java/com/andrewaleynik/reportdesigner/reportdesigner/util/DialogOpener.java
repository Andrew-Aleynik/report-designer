package com.andrewaleynik.reportdesigner.reportdesigner.util;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.controllers.DialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public final class DialogOpener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DialogOpener.class);

    public record OpenResult<C>(C controller, boolean saved) {
    }

    private DialogOpener() {
    }

    public static <C> Optional<OpenResult<C>> open(String fxmlPath, String title, Node owner) {
        return open(fxmlPath, title, owner, null);
    }

    public static <C> Optional<OpenResult<C>> open(String fxmlPath, String title, Node owner,
                                                   Consumer<C> configure) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogOpener.class.getResource(fxmlPath));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            C controller = loader.getController();

            Stage stage = createStage(title, owner, root);

            if (controller instanceof DialogController dialogController) {
                dialogController.setDialogStage(stage);
            }

            if (configure != null) {
                configure.accept(controller);
            }

            stage.showAndWait();

            boolean saved = controller instanceof DialogController dialogController && dialogController.isSaved();
            return Optional.of(new OpenResult<>(controller, saved));
        } catch (IOException e) {
            LOGGER.error("Error opening dialog '{}': {}", title, e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
            return Optional.empty();
        }
    }

    private static Stage createStage(String title, Node owner, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null && owner.getScene() != null) {
            stage.initOwner(owner.getScene().getWindow());
        }
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        return stage;
    }
}
